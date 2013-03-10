package metridoc.core

import grails.plugin.quartz2.GrailsArtefactJob
import groovy.grape.Grape
import metridoc.utils.ConfigObjectUtils
import metridoc.utils.JobTrigger
import metridoc.utils.QuartzUtils
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.SystemUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.commons.lang.text.StrBuilder
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher

import java.util.concurrent.TimeUnit

import static metridoc.core.QuartzMonitorJobFactory.QuartzDisplayJob.JOB_STATUS.COMPLETE
import static metridoc.core.QuartzMonitorJobFactory.QuartzDisplayJob.JOB_STATUS.ERROR

class QuartzService {

    static final GROOVY_VERSION = "2.0.5"
    static final GROOVY_DISTRIBUTION = "http://dist.groovy.codehaus.org/distributions/groovy-binary-${GROOVY_VERSION}.zip"
    private static final String DISABLE_GRAPE = 'groovy.grape.enable'
    def quartzScheduler
    def grailsApplication
    def pluginManager
    def commonService
    def mailService
    def logService

    /**
     * Checks if a trigger is manual
     *
     * @deprecated
     * @param trigger
     * @return
     */
    static boolean isManual(Trigger trigger) {
        QuartzUtils.isManual(trigger)
    }

    def mailJobError(Throwable throwable, JobExecutionContext context) {
        def trigger = context.trigger
        String triggerName = trigger.key.name
        String jobName = trigger.jobKey.name
        String shortErrorMessage = "error occurred running job ${jobName} with trigger ${triggerName} will notify interested users by email"

        def emails = NotificationEmails.findByScope(QuartzController.JOB_FAILURE_SCOPE).collect { it.email }
        def emailIsConfigured = commonService.emailIsConfigured() && emails && !NotificationEmailsService.emailDisabledFromSystemProperty()
        if (emailIsConfigured) {
            emails.each { email ->
                try {
                    log.info "sending email to ${email} about ${jobName} failure"
                    mailService.sendMail {
                        subject shortErrorMessage
                        text ExceptionUtils.getFullStackTrace(throwable)
                        to email
                    }
                } catch (Throwable emailError) {
                    log.error("could not send email to ${email}", emailError)
                }
            }

        } else {
            log.info "could not send email about ${jobName} failure since email is not configured or is disabled"
        }
    }

    static List<String> getTriggerSchedules() {
        QuartzUtils.triggerSchedules
    }

    /**
     * gets a {@link JobDetails} if it exists, otherwise returns a new one with the trigger name
     * @param triggerName
     * @return the job config associated with the trigger name
     */
    JobDetails getJobDetailsByTrigger(String triggerName) {
        def jobDetails = JobDetails.findByJobName(triggerName)
        if (jobDetails) return jobDetails

        return new JobDetails(jobName: triggerName)
    }

    org.quartz.Trigger getTrigger(String triggerName) {
        QuartzUtils.getTrigger(quartzScheduler, triggerName)
    }

    /**
     * checks if groovy has been downloaded to run jobs, if not it is downloaded under metridoc home
     * Not sure we need this anymore, we may want to have this available to deal with multiple distros
     */
    void checkForGroovyDistribution() {
        def metridocHome = grailsApplication.mergedConfig.metridoc?.home ?: "${SystemUtils.USER_HOME}/.metridoc"
        def groovyHome = "$metridocHome/groovy"
        def groovyDirectoryPath = "$groovyHome/groovy-$GROOVY_VERSION"
        def groovyDirectory = new File(groovyDirectoryPath)

        if (!groovyDirectory.exists()) {
            assert groovyDirectory.mkdirs(): "Could not create the groovy distribution directory"
            log.info "groovy distribution is not in metridoc home, downloading now, this could take several minutes"
            def groovyFile = new File("${groovyDirectoryPath}.zip")
            groovyFile.delete()
            assert groovyFile.createNewFile(): "Could not create ${groovyFile}"
            groovyFile << new URL(GROOVY_DISTRIBUTION).newInputStream()
            log.info "unzipping the groovy distribution"
            def ant = new AntBuilder()
            ant.unzip(
                    src: groovyFile,
                    dest: groovyDirectory
            )
        }
    }

    void rescheduleJob(String triggerName, String triggerDescription) {
        def plugin = pluginManager.getGrailsPluginForClassName("Quartz2GrailsPlugin").instance
        Closure schedulerJob = plugin.scheduleJob
        schedulerJob.delegate = plugin

        def jobDetails = getSchedule(triggerName, triggerDescription)
        jobDetails.jobTrigger = JobTrigger.valueOf(triggerDescription)
        jobDetails.save(flush: true, failOnError: true)
        org.quartz.Trigger newTrigger
        if (triggerDescription == "NEVER") {
            long fiftyYears = TimeUnit.DAYS.toMillis(365 * 50)
            newTrigger = jobDetails.convertTriggerToQuartzTrigger()
            newTrigger.startTime = new Date(new Date().time + fiftyYears)
        } else {
            newTrigger = jobDetails.convertTriggerToQuartzTrigger()
            newTrigger.startTime = new Date()
        }

        def key = new TriggerKey(triggerName)
        newTrigger.key = key
        quartzScheduler.rescheduleJob(key, newTrigger)
    }

    JobDetails getSchedule(String triggerName, String description) {
        def schedule = JobDetails.findByJobName(triggerName)
        if (schedule) return schedule

        return new JobDetails(jobName: triggerName, jobTrigger: JobTrigger.valueOf(description))
    }

    ConfigObject getConfigByTriggerName(String triggerName) {
        ConfigObject mergedConfig = grailsApplication.mergedConfig
        ConfigObject clonedConfig = ConfigObjectUtils.clone(mergedConfig)
        if (triggerName) {
            return getConfigurationMergedWithAppConfig(clonedConfig, triggerName)
        }

        return mergedConfig
    }

    static void addConfigToBinding(Map config, Binding binding) {
        if (config) {
            binding.setVariable("config", config)
        }
    }

    Map getJobListModel() {
        getJobListModel(null, [] as Set)
    }

    void eachTrigger(Closure closure) {
        QuartzUtils.eachTrigger(quartzScheduler, closure)
    }

    Map getJobListModel(badEmails, Set alerts) {
        def jobsList = []
        def listJobGroups = quartzScheduler.getJobGroupNames()

        listJobGroups?.each { jobGroup ->
            quartzScheduler.getJobKeys(GroupMatcher.groupEquals(jobGroup))?.each { jobKey ->
                def triggers = quartzScheduler.getTriggersOfJob(jobKey)
                if (triggers) {
                    triggers.each { org.quartz.Trigger trigger ->
                        def currentJob = createJob(jobGroup, jobKey.name, jobsList, trigger.key.name)
                        currentJob.trigger = trigger
                        currentJob.manualJob = QuartzUtils.isManual(trigger)
                        currentJob.triggerStatus = quartzScheduler.getTriggerState(trigger.key)
                    }
                } else {
                    createJob(jobGroup, jobKey.name, jobsList)
                }
            }
        }

        def notificationEmails = new StrBuilder()
        NotificationEmails.list().collect { it.email }.each {
            notificationEmails.appendln(it)
        }
        def badEmailMessage = null
        if (badEmails) {
            def badEmailMessageBuilder = new StrBuilder("The following emails are not valid: ")
            if (badEmails instanceof String) {
                badEmailMessageBuilder.append(badEmails)
                badEmailMessage = badEmailMessageBuilder.toString()
            } else {
                badEmails.each {
                    badEmailMessageBuilder.append(it)
                    badEmailMessageBuilder.append(", ")
                }
                badEmailMessage = badEmailMessageBuilder.toString()
                badEmailMessage = StringUtils.chop(badEmailMessage)
                badEmailMessage = StringUtils.chop(badEmailMessage)
            }
        }

        if (badEmailMessage) {
            alerts << badEmailMessage
        }

        if (NotificationEmails.count() == 0) {
            alerts << "No emails have been set, no one will be notified of job failures"
        }
        if (!commonService.emailIsConfigured()) {
            alerts << "Email has not been set up properly, no notifications will be sent on job failures"
        }

        return [
                jobs: jobsList,
                now: new Date(),
                scheduler: quartzScheduler,
                notificationEmails: notificationEmails,
        ]
    }

    void saveRun(QuartzMonitorJobFactory.QuartzDisplayJob displayJob) {
        def jobRuns = JobRuns.createJobRun(displayJob)
        def out = new ByteArrayOutputStream()
        logService.renderDefaultLogStartingAt(out, jobRuns.start)
        jobRuns.jobLog = out.toByteArray()
        jobRuns.save()
    }

    private QuartzMonitorJobFactory.QuartzDisplayJob createJob(String jobGroup, String jobName, List<QuartzMonitorJobFactory.QuartzDisplayJob> jobsList, String triggerName = "") {
        def displayJob = QuartzMonitorJobFactory.jobRuns.get(triggerName)
        if (!displayJob) {
            displayJob = new QuartzMonitorJobFactory.QuartzDisplayJob()
            displayJob.setJobKey(new JobKey(jobName, jobGroup))
            if (triggerName) {
                def mostRecentRun = getMostRecentRun(triggerName)
                displayJob.lastRun = getLastRun(triggerName)
                displayJob.duration = getLastDuration(triggerName)
                if (mostRecentRun) {
                    displayJob.status = COMPLETE
                    if (mostRecentRun.error) {
                        displayJob.error = "[Unknown error occurred, look at logs]"
                        displayJob.status = ERROR
                    }
                }
                displayJob.addToolTip()
                displayJob.quartzService = this
            }
        }

        jobsList.add(displayJob)
        return displayJob
    }

    boolean isRemoteScriptJob(String jobName) {
        return JobDetails.findByJobName(jobName)?.url != null
    }

    Job buildRemoteScriptJob(String jobName) {
        //we do not want to deal with grape issues, this could pollute our classpath
        Grape.setEnableGrapes(false)
        def jobDetails = JobDetails.findByJobName(jobName)
        String url = jobDetails.url
        try {
            def shell = new GroovyShell()
            int slashLocation = url.lastIndexOf("/")
            def baseUrl = url.substring(0, slashLocation + 1)
            shell.classLoader.addURL(new URL(baseUrl))
            def content = jobDetails.convertUrlToContent()
            def script = shell.parse(content)
            def scriptJob = new ScriptJob(script)
            return new GrailsArtefactJob(scriptJob)
        } catch (Throwable throwable) {
            log.error("Could not build the remote script $url", throwable)
            throw throwable
        }
    }

    Job buildJob(String grailsJobName) {
        def applicationContext = grailsApplication.mainContext
        Object jobBean = applicationContext.getBean(grailsJobName);
        Job job
        if (jobBean instanceof Script) {
            ScriptJob scriptJob = new ScriptJob((Script) jobBean);
            job = new GrailsArtefactJob(scriptJob);
        } else {
            job = new GrailsArtefactJob(jobBean);
        }

        return job
    }

    //ADD EVERYTHING STARTING HERE AND BELOW TO QUARTZUTILS
    static Date getLastRun(String jobName) {
        JobRuns jobRun = getMostRecentRun(jobName)
        if (jobRun) {
            return new Date(jobRun.start)
        }

        return null
    }

    private static ConfigObject getConfigurationMergedWithAppConfig(ConfigObject applicationConfiguration, String triggerName) {
        def jobConfig = JobDetails.findByJobName(triggerName)
        if (jobConfig) {
            return getConfigurationMergedWithAppConfig(applicationConfiguration, jobConfig.generateConfigObject())
        } else {
            return applicationConfiguration
        }
    }

    private static ConfigObject getConfigurationMergedWithAppConfig(ConfigObject applicationConfiguration, ConfigObject override) {

        if (override) {
            applicationConfiguration.merge(override)
        }

        return applicationConfiguration
    }

    static Long getLastDuration(String jobName) {
        JobRuns jobRun = getMostRecentRun(jobName)

        if (jobRun) {
            return jobRun.finish - jobRun.start
        }

        return null
    }

    static JobRuns getMostRecentRun(String jobName) {
        List<JobRuns> runs = JobRuns.where {
            details.jobName == jobName

        }.order("start", "desc").findAll()

        if (runs) {
            return runs.get(0) as JobRuns
        }

        return null
    }



    void configureJob(String name, GrailsArtefactJob job) {
        def jobInstance = job.job
        if (jobInstance instanceof ScriptJob) {
            def jobDetails = JobDetails.findByJobName(name)
            jobInstance.arguments = jobDetails.arguments
        }

    }
}
