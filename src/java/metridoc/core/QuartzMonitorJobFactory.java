package metridoc.core;

import grails.plugin.quartz2.GrailsArtefactJob;
import grails.plugin.quartz2.GrailsArtefactJobDetailFactoryBean;
import groovy.lang.Script;
import groovy.util.ConfigObject;
import org.hibernate.SessionFactory;
import org.quartz.*;
import org.quartz.simpl.PropertySettingJobFactory;
import org.quartz.spi.OperableTrigger;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Date;
import java.util.HashMap;

/**
 * The quartz monitor factory used to generate jobs for MetriDoc
 */
public class QuartzMonitorJobFactory extends PropertySettingJobFactory implements ApplicationContextAware {

    static final java.util.Map<String, QuartzDisplayJob> jobRuns = new HashMap<String, QuartzDisplayJob>();
    private SessionFactory sessionFactory;
    private ApplicationContext applicationContext;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * creates a job based on the passed {@link TriggerFiredBundle}.
     *
     * @param bundle    the bundle to base the job off of
     * @param scheduler the quartz scheduler
     * @return the job
     * @throws SchedulerException
     */
    @Override
    public org.quartz.Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        OperableTrigger trigger = bundle.getTrigger();
        String triggerName = trigger.getKey().getName();
        JobDataMap jobDataMap = bundle.getJobDetail().getJobDataMap();
        JobDataMap triggerJobDataMap = trigger.getJobDataMap();
        QuartzService quartzService = applicationContext.getBean("quartzService", QuartzService.class);
        ConfigObject config = quartzService.getConfigByTriggerName(triggerName);
        triggerJobDataMap.put("config", config);
        String grailsJobName = (String) jobDataMap.get(GrailsArtefactJobDetailFactoryBean.JOB_NAME_PARAMETER);
        org.quartz.Job job;

        if (grailsJobName != null) {
            job = quartzService.buildJob(grailsJobName);
        } else if(quartzService.isRemoteScriptJob(triggerName)) {
            job = quartzService.buildRemoteScriptJob(triggerName);
        } else {
            job = super.newJob(bundle, scheduler);
        }

        if (job instanceof GrailsArtefactJob) {
            QuartzDisplayJob previousRun;
            if (jobRuns.containsKey(triggerName)) {
                previousRun = jobRuns.get(triggerName);
            } else {
                previousRun = new QuartzDisplayJob();
            }

            QuartzDisplayJob displayJob;
            try {
                displayJob = (QuartzDisplayJob) previousRun.clone();
            } catch (CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            displayJob.setQuartzService(quartzService);
            displayJob.setSessionFactory(sessionFactory);
            displayJob.setJob((GrailsArtefactJob) job);
            displayJob.setJobKey(trigger.getJobKey());
            displayJob.setTrigger(trigger);
            job = displayJob;
            jobRuns.put(triggerName, displayJob);
        }
        return job;
    }

    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * Quartz Job implementation that invokes execute() on the GrailsTaskClassJob instance whilst recording the time
     */
    public static class QuartzDisplayJob implements org.quartz.Job, Cloneable {
        static final Logger displayLogger = LoggerFactory.getLogger(QuartzDisplayJob.class);
        private QuartzService quartzService;
        private Long duration;
        private boolean interrupting;
        private JOB_STATUS status;
        private Date lastRun;
        private String error;
        private String tooltip;
        private Trigger.TriggerState triggerStatus;
        private Trigger trigger;
        private boolean manualJob = false;

        public enum JOB_STATUS {RUNNING, COMPLETE, ERROR, INTERRUPTING}

        GrailsArtefactJob job;
        private SessionFactory sessionFactory;
        private final Logger log = LoggerFactory.getLogger(QuartzDisplayJob.class);
        private JobKey jobKey;

        public QuartzDisplayJob(GrailsArtefactJob job, SessionFactory sessionFactory) {
            this.job = job;
            this.sessionFactory = sessionFactory;
        }

        public QuartzDisplayJob() {
        }

        public void execute(final JobExecutionContext context) throws JobExecutionException {
            try {
                Long lastDuration = null;
                String jobName = trigger.getKey().getName();
                lastDuration = quartzService.getLastDuration(jobName);
                Date lastRunUsed = new Date();
                String lastTooltip = getTooltip();
                clearDetails();
                setDuration(lastDuration);
                setTooltip(lastTooltip);
                setLastRun(lastRunUsed);
                setStatus(JOB_STATUS.RUNNING);
                long start = System.currentTimeMillis();
                String error;
                try {
                    displayLogger.info("starting job {} at {}", getTrigger().getKey().getName(), getLastRun());
                    quartzService.configureJob(trigger.getKey().getName(), job);
                    job.execute(context);
                    sessionFactory.getCurrentSession().flush();
                } catch (Throwable e) {
                    quartzService.mailJobError(e, context);
                    error = e.getMessage();
                    setError(error);
                    setStatus(JOB_STATUS.ERROR);
                    addDurationAndToolTip(start);
                    displayLogger.error("Exception occurred running job " + getTrigger().getKey().getName(), e);

                    if (e instanceof JobExecutionException) {
                        throw (JobExecutionException) e;
                    }
                    throw new JobExecutionException(e.getMessage(), e);
                }
                setStatus(JOB_STATUS.COMPLETE);
                addDurationAndToolTip(start);
            } catch (Exception e) {
                //this is really annoying that we have to log this twice (aka quartz will log this too)!, but for the logging per job to work properly this must happen

                if (e instanceof JobExecutionException) {
                    throw (JobExecutionException) e;
                }
                throw new JobExecutionException(e.getMessage(), e);
            } finally {
                org.quartz.Trigger currentTrigger = context.getTrigger();
                try {
                    setInterrupting(false);
                } catch (UnableToInterruptJobException e) {
                    throw new JobExecutionException(e);
                }
                JobDataMap jobData = currentTrigger.getJobDataMap();
                if (jobData.containsKey("oldTrigger")) {
                    try {
                        org.quartz.Trigger oldTrigger = (org.quartz.Trigger) jobData.get("oldTrigger");
                        if (oldTrigger != null) {
                            context.getScheduler().rescheduleJob(context.getTrigger().getKey(), oldTrigger);
                        } else {
                            context.getScheduler().unscheduleJob(currentTrigger.getKey());
                        }
                    } catch (SchedulerException e) {
                        log.error("Could not reschedule trigger " + currentTrigger.getKey().getName(), e);
                    }
                }
                try {
                    quartzService.saveRun(this);
                } catch (Exception e) {
                    displayLogger.error("Could not save run", e);
                }
            }

        }

        public void addDurationAndToolTip(long start) {
            long duration = System.currentTimeMillis() - start;
            setDuration(duration);
            addToolTip();
        }

        public void addToolTip() {
            String jobRunTime = "Most recent job ran in " + duration + "ms";
            String jobException = error != null ? ", with error " + error : "";
            String tooltip = jobRunTime + jobException;
            setTooltip(tooltip);
        }

        public void setDuration(Long duration) {
            this.duration = duration;
        }

        public Long getDuration() {
            return this.duration;
        }

        public boolean canInterrupt() {
            return status == JOB_STATUS.RUNNING;
        }

        public void setInterrupting(boolean interrupting) throws UnableToInterruptJobException {
            this.interrupting = interrupting;
            if (interrupting) {
                job.interrupt();
            }
        }

        public Boolean getInterrupting() {
            return this.interrupting;
        }

        public void setStatus(JOB_STATUS status) {
            this.status = status;
        }

        public String getStatus() {
            if (this.status != null) {
                return this.status.toString().toLowerCase();
            }

            return null;
        }

        public Date getLastRun() {
            return this.lastRun;
        }

        public void setLastRun(Date lastRun) {
            this.lastRun = lastRun;
        }

        public void clearDetails() {
            this.lastRun = null;
            this.status = null;
            this.duration = null;
            this.error = null;
        }

        public String getError() {
            return this.error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public String getTooltip() {
            return this.tooltip;
        }

        public void setTooltip(String tooltip) {
            this.tooltip = tooltip;
        }

        public String getJobGroup() {
            if (jobKey != null) {
                return jobKey.getGroup();
            }
            return null;
        }

        public String getJobName() {
            if (jobKey != null) {
                return jobKey.getName();
            }
            return null;
        }

        public JobKey getJobKey() {
            return jobKey;
        }

        public void setJobKey(JobKey jobKey) {
            this.jobKey = jobKey;
        }

        public GrailsArtefactJob getJob() {
            return job;
        }

        public void setJob(GrailsArtefactJob job) {
            this.job = job;
        }

        public SessionFactory getSessionFactory() {
            return sessionFactory;
        }

        public void setSessionFactory(SessionFactory sessionFactory) {
            this.sessionFactory = sessionFactory;
        }

        public Trigger.TriggerState getTriggerStatus() {
            return triggerStatus;
        }

        public void setTriggerStatus(Trigger.TriggerState triggerStatus) {
            this.triggerStatus = triggerStatus;
        }

        public Trigger getTrigger() {
            return trigger;
        }

        public void setTrigger(Trigger trigger) {
            this.trigger = trigger;
        }

        public boolean isManualJob() {
            return manualJob;
        }

        public void setManualJob(boolean manualJob) {
            this.manualJob = manualJob;
        }

        public QuartzService getQuartzService() {
            return quartzService;
        }

        public void setQuartzService(QuartzService quartzService) {
            this.quartzService = quartzService;
        }

        @Override
        protected Object clone() throws CloneNotSupportedException {
            QuartzDisplayJob clone = new QuartzDisplayJob();
            clone.duration = duration;
            clone.status = status;
            clone.lastRun = lastRun;
            clone.error = error;
            clone.interrupting = interrupting;

            return clone;
        }
    }
}
