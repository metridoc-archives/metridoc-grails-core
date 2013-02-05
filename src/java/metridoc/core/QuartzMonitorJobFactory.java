package metridoc.core;

import grails.plugin.quartz2.GrailsArtefactJob;
import grails.plugin.quartz2.GrailsJobFactory;
import org.hibernate.SessionFactory;
import org.quartz.*;
import org.quartz.spi.TriggerFiredBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 11/6/12
 * Time: 7:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class QuartzMonitorJobFactory extends GrailsJobFactory {

    static final java.util.Map<String, Map<String, Object>> jobRuns = new HashMap<String, Map<String, Object>>();
    private SessionFactory sessionFactory;
    private ApplicationContext applicationContext;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public Job newJob(TriggerFiredBundle bundle, Scheduler scheduler) throws SchedulerException {
        //String grailsJobName = bundle.getJobDetail().getName();
        String grailsJobName = bundle.getTrigger().getKey().getName();

        Job job = super.newJob(bundle, scheduler);
        if (job instanceof GrailsArtefactJob) {
            Map<String, Object> map;
            if (jobRuns.containsKey(grailsJobName)) {
                map = jobRuns.get(grailsJobName);
            } else {
                map = new HashMap<String, Object>();
                jobRuns.put(grailsJobName, map);
            }
            job = new QuartzDisplayJob((GrailsArtefactJob) job, map, sessionFactory);
        }
        return job;
    }

    /**
     * Quartz Job implementation that invokes execute() on the GrailsTaskClassJob instance whilst recording the time
     */
    public class QuartzDisplayJob implements Job {
        GrailsArtefactJob job;
        Map<String, Object> jobDetails;
        private SessionFactory sessionFactory;
        private final Logger log = LoggerFactory.getLogger(QuartzDisplayJob.class);

        public QuartzDisplayJob(GrailsArtefactJob job, Map<String, Object> jobDetails, SessionFactory sessionFactory) {
            this.job = job;
            this.jobDetails = jobDetails;
            this.sessionFactory = sessionFactory;
        }

        public void execute(final JobExecutionContext context) throws JobExecutionException {
            try {
                Object lastDuration = jobDetails.get("duration");
                Object lastTooltip = jobDetails.get("tooltip");
                jobDetails.clear();
                if (lastDuration != null) {
                    jobDetails.put("lastDuration", lastDuration);
                }
                if (lastTooltip != null) {
                    jobDetails.put("tooltip", lastTooltip);
                }
                jobDetails.put("lastRun", new Date());
                jobDetails.put("status", "running");
                long start = System.currentTimeMillis();
                String error = null;
                try {
                    job.execute(context);
                    sessionFactory.getCurrentSession().flush();
                } catch (Throwable e) {
                    error = e.getMessage();
                    jobDetails.put("error", error);
                    jobDetails.put("status", "error");
                    addDurationAndToolTip(jobDetails, start);
                    log.error("error occurred running job " + context.getJobDetail().getKey() + " with trigger " + context.getTrigger().getKey(), e);
                    if (e instanceof JobExecutionException) {
                        throw (JobExecutionException) e;
                    }
                    throw new JobExecutionException(e.getMessage(), e);
                }
                jobDetails.put("status", "complete");
                addDurationAndToolTip(jobDetails, start);
            } finally {
                Trigger currentTrigger = context.getTrigger();
                JobDataMap jobData = currentTrigger.getJobDataMap();
                if (jobData.containsKey("oldTrigger")) {
                    try {
                        Trigger oldTrigger = (Trigger) jobData.get("oldTrigger");
                        if (oldTrigger != null) {
                            context.getScheduler().rescheduleJob(context.getTrigger().getKey(), oldTrigger);
                        } else {
                            context.getScheduler().unscheduleJob(currentTrigger.getKey());
                        }
                    } catch (SchedulerException e) {
                        log.error("Could not reschedule trigger " + currentTrigger.getKey().getName(), e);
                    }
                }
            }

        }

        public void addDurationAndToolTip(Map<String, Object> jobDetails, long start) {
            long duration = System.currentTimeMillis() - start;
            String error = (String) jobDetails.get("error");
            jobDetails.put("duration", duration);
            String jobRunTime = "Most recent job ran in " + duration + "ms";
            String jobException = error != null ? ", with error " + error : "";
            String tooltip = jobRunTime + jobException;
            jobDetails.put("tooltip", tooltip);
        }
    }
}
