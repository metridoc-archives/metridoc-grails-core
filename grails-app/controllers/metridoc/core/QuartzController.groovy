package metridoc.core

import grails.converters.JSON
import grails.web.RequestParameter
import metridoc.utils.JobTrigger
import metridoc.utils.QuartzUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.quartz.*

import static org.quartz.utils.Key.DEFAULT_GROUP

/**
 * Manages the installed jobs in metridoc
 */
class QuartzController {

    private static final String LIST = "list"
    private static final Map TO_LIST = [action: LIST]
    static final String JOB_FAILURE_SCOPE = "jobFailure"
    public static final String NO_DESCRIPTION = "No description available"
    def initQuartzService

    /**
     * By default only admins can access this controller
     */
    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    static homePage = [
            title: "Job List",
            adminOnly: true,
            description: """
                List of all available jobs and their next scheduled run.  All jobs can be manually ran on this
                page as well.
            """
    ]

    Scheduler quartzScheduler
    def quartzService

    /**
     * saves all settings.  Synchronized to avoid any weird behavior from multiple editors submitting at the same time
     */
    synchronized saveSettings() {
        String emails = params.emails
        def badEmails = []
        if (emails) {
            badEmails = NotificationEmails.storeEmails(JOB_FAILURE_SCOPE, emails)
        }

        redirect(action: "index", params: [badEmails: badEmails])
    }

    def index() {
        redirect(action: LIST, params: params)
    }

    def list() {
        quartzService.getJobListModel(params.badEmails, flash.alerts)
    }

    def pause(@RequestParameter("id") String jobName) {
        quartzScheduler.pauseJob(jobKey(jobName, DEFAULT_GROUP))
        redirect(action: LIST)
    }

    def resume(@RequestParameter("id") String jobName) {
        quartzScheduler.resumeJob(jobKey(jobName, DEFAULT_GROUP))
        redirect(action: LIST)
    }

    def deleteJob(@RequestParameter("id") String jobName) {
        doTriggerOperation(jobName) {
            def runs = JobRuns.where {
                details.jobName == jobName
            }.findAll()
            runs.each {
                it.delete()
            }
            def jobDetails = JobDetails.findByJobName(jobName)
            jobDetails.delete()
            quartzScheduler.unscheduleJob(new TriggerKey(jobName))
            redirect(TO_LIST)
        }
    }

    def runNow(@RequestParameter("id") String triggerName) {
        doTriggerOperation(triggerName) { Trigger trigger ->
            use(QuartzUtils) {
                def dataMap = new JobDataMap(params)
                quartzScheduler.triggerJobFromTrigger(trigger, dataMap)
            }

            Thread.sleep(1000) //sleep for 1 second to allow for the job to actually start
            redirect(action: LIST)
        }
    }

    private doTriggerOperation(String triggerName, Closure operation) {
        def result = null
        if (triggerName) {
            use(QuartzUtils) {
                Trigger trigger = quartzScheduler.searchForTrigger(triggerName)
                if (trigger) {
                    result = operation.call(trigger)
                } else {
                    errorChain("Could not find trigger for trigger name ${triggerName}")
                }
            }
        } else {
            errorChain("Could not perform job operation, triggerName was not specified")
        }

        return result
    }

    private void errorChain(String errorMessage) {
        flash.alerts << errorMessage
        redirect(action: LIST)
    }

    def status() {
        String triggerId = params.id
        if (triggerId) {
            def jobInfo = QuartzMonitorJobFactory.jobRuns[triggerId]
            if (jobInfo) {
                render jobInfo as JSON
            } else {
                render(status: 404, text: "job with trigger $triggerId does not exist")
            }
        } else {
            render QuartzMonitorJobFactory.jobRuns as JSON
        }
    }

    def jobTableOnly() {
        quartzService.getJobListModel()
    }

    def startScheduler() {
        quartzScheduler.start()
        redirect(action: LIST)
    }

    def stopScheduler() {
        quartzScheduler.standby()
        redirect(action: LIST)
    }

    def stopJob(@RequestParameter("id") String triggerName) {
        doTriggerOperation(triggerName) {
            def displayJob = QuartzMonitorJobFactory.jobRuns.get(triggerName)
            if (displayJob == null) {
                flash.alert = "job $triggerName does not exist"
                redirect(action: LIST)
                return
            }

            try {
                if (displayJob.canInterrupt()) {
                    displayJob.interrupting = true;
                } else {
                    flash.infos << "Could not interrupt job $triggerName, it probably finished before the stop command was issued"
                }
            } catch (UnableToInterruptJobException e) {
                flash.alert = e.message
            }
        }
        redirect(action: LIST)
    }

    def show(@RequestParameter("id") String triggerName, String errorConfig, String errorCron) {
        doTriggerOperation(triggerName) { Trigger trigger ->
            def jobDetails = JobDetails.findByJobName(triggerName)
            def currentSchedule = jobDetails.jobTrigger
            boolean manual = QuartzUtils.isManual(trigger)
            if (manual) {
                currentSchedule = metridoc.trigger.Trigger.NEVER
            }
            def triggerSchedules = quartzService.triggerSchedules
            if (JobTrigger.IN_CODE != currentSchedule) {
                triggerSchedules.remove(JobTrigger.IN_CODE.toString())
            }

            def config = errorConfig
            def cron = errorCron
            if (!errorConfig) {
                //if we dont have the config then get it from the database
                def jobConfig = JobDetails.findByJobName(triggerName)
                config = jobConfig ? jobConfig.config : null
            }

            if (!errorCron) {
                cron = jobDetails.cron
            }
            if (!trigger) return
            def jobLog = null
            def mostRecentRun = QuartzService.getMostRecentRun(triggerName)
            if (mostRecentRun) {
                jobLog = new ByteArrayInputStream(mostRecentRun.jobLog)
            }

            return [
                    description: jobDetails.description ?: NO_DESCRIPTION,
                    isScriptJob: jobDetails.url ? true : false,
                    scriptUrl: jobDetails.url,
                    cron: cron,
                    jobLog: jobLog,
                    config: config,
                    trigger: trigger,
                    currentSchedule: currentSchedule,
                    triggerName: trigger.key.name,
                    nextFireTime: trigger.nextFireTime,
                    availableSchedules: triggerSchedules,
                    arguments: jobDetails ? jobDetails.arguments : null
            ]
        }
    }

    def updateSchedule(@RequestParameter("id") String triggerName, String availableSchedules, String config,
                       String arguments, String customCron) {

        doTriggerOperation(triggerName) { Trigger trigger ->

            JobDetails jobDetails = quartzService.getJobDetailsByTrigger(triggerName)
            jobDetails.config = config
            jobDetails.arguments = arguments
            jobDetails.cron = customCron
            jobDetails.save()

            if (jobDetails && jobDetails.errors.errorCount) {
                if (jobDetails.errors.getFieldError("config")) {
                    Throwable exception = JobDetails.getConfigException(config)
                    flash.alerts << "<pre>${ExceptionUtils.getStackTrace(exception)}</pre>"
                    log.error "Tried to store a bad configuration", exception
                } else if (jobDetails.errors.getFieldError("cron")) {
                    flash.alerts << "cron $jobDetails.cron is not a valid cron"
                    log.error "Tried to store illegal cron $jobDetails.cron"
                } else {
                    flash.alerts << "Unknown exception trying to save configuration"
                    log.error "Could not store job config: ${jobDetails.errors}"
                }

                def params = [errorConfig: config, errorCron: customCron, id: triggerName]
                redirect(action: "show", params: params)
                return
            }

            quartzService.rescheduleJob(triggerName, availableSchedules)
            flash.messages << "made updates to job $triggerName" as String
            chain(action: LIST)
        }
    }

    @SuppressWarnings("GroovyUnusedCatchParameter")
    def newJob(String url, String jobName) {
        if (!url || !jobName) {
            errorChain("url and job name must not be blank or null")
            return
        }

        def usedUrl
        try {
            usedUrl = new URL(url)
        } catch (MalformedURLException ignore) {
            errorChain("url $url is not a valid url")
            return
        }

        def scriptText = usedUrl.text
        try {
            new GroovyShell().parse(scriptText)
        } catch (Throwable throwable) {
            errorChain("Could not parse the script!<br /><pre>${ExceptionUtils.getStackTrace(throwable).encodeAsHTML()}</pre>")
            return
        }

        def details = JobDetails.findByJobName(jobName)
        if (details) {
            errorChain("${jobName} already exists")
            return
        }

        def template = url.substring(url.lastIndexOf("/") + 1)
        details = new JobDetails(jobName: jobName, url: url, template: template, jobTrigger: JobTrigger.NEVER)
        details.save()
        flash.infos << "Job $jobName has been added"
        initQuartzService.handleUrlBasedScripts()
        redirect(action: LIST)
    }

    def saveDescription(@RequestParameter("id") String jobName, String description) {
        def jobDetails
        doTriggerOperation(jobName) {
            jobDetails = JobDetails.findByJobName(jobName)
            jobDetails.description = description
            jobDetails.save(failOnError: true)
        }

        redirect(action: "show", id: jobName)
    }

    private static jobKey(name, group) {
        return JobKey.jobKey(name, group)
    }
}
