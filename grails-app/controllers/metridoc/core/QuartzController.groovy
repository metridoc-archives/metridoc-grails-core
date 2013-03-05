package metridoc.core

import grails.converters.JSON
import grails.web.RequestParameter
import org.apache.commons.lang.exception.ExceptionUtils
import org.quartz.*

/**
 * Manages the installed jobs in metridoc
 */
class QuartzController {

    private static final String LIST = "list"
    static final String JOB_FAILURE_SCOPE = "jobFailure"

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

    static final Map triggers = [:]

    def quartzScheduler
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

    def start() {
        def trigger = triggers.get(params.jobName)
        quartzScheduler.scheduleJob(trigger)
        redirect(action: LIST)
    }

    def pause() {
        quartzScheduler.pauseJob(jobKey(params.jobName, params.jobGroup))
        redirect(action: LIST)
    }

    def resume() {
        quartzScheduler.resumeJob(jobKey(params.jobName, params.jobGroup))
        redirect(action: LIST)
    }

    def runNow(@RequestParameter("id") String triggerName) {
        def trigger = searchForTrigger(triggerName)
        if (!trigger) return

        def triggerKey = new TriggerKey(triggerName)
        def dataMap = new JobDataMap(params)

        dataMap.oldTrigger = quartzScheduler.getTrigger(triggerKey)
        quartzService.triggerJobFromTrigger(trigger, dataMap)

        Thread.sleep(1000) //sleep for 1 second to allow for the job to actually start
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

    def jobTableOnly(){
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

    def stopJob(@RequestParameter("id") String jobName) {
        def displayJob = QuartzMonitorJobFactory.jobRuns.get(jobName)
        if (displayJob == null) {
            flash.alert = "job $jobName does not exist"
            redirect(action: LIST)
            return
        }

        try {
            if (displayJob.canInterrupt()) {
                displayJob.interrupting = true;
            } else {
                flash.infos << "Could not interrupt job $jobName, it probably finished before the stop command was issued"
            }
        } catch (UnableToInterruptJobException e) {
            flash.alert = e.message
        }
        redirect(action: LIST)
    }

    def show(@RequestParameter("id") String triggerName, String errorConfig) {
        org.quartz.Trigger trigger = searchForTrigger(triggerName)
        def jobSchedule = JobDetails.findByJobName(triggerName)
        def currentSchedule = jobSchedule ? jobSchedule.jobTrigger.toString() : "DEFAULT"
        boolean manual = QuartzService.isManual(trigger)
        if (manual) {
            currentSchedule = metridoc.trigger.Trigger.NEVER
        }
        def triggerSchedules = quartzService.triggerSchedules
        if ("DEFAULT" != currentSchedule) {
            triggerSchedules.remove("DEFAULT")
        }

        def config = errorConfig
        if (!errorConfig) {
            //if we dont have the config then get it from teh database
            def jobConfig = JobDetails.findByJobName(triggerName)
            config = jobConfig ? jobConfig.config : null
        }
        if (!trigger) return
        def jobLog = null
        def mostRecentRun = QuartzService.getMostRecentRun(triggerName)
        if (mostRecentRun) {
            jobLog = new ByteArrayInputStream(mostRecentRun.jobLog)
        }
        return [
                jobLog: jobLog,
                config: config,
                trigger: trigger,
                currentSchedule: currentSchedule,
                triggerName: trigger.key.name,
                nextFireTime: trigger.nextFireTime,
                availableSchedules: triggerSchedules
        ]

    }

    def updateSchedule(@RequestParameter("id") String triggerName, String availableSchedules, String config) {
        org.quartz.Trigger trigger = searchForTrigger(triggerName)
        if (!trigger) return

        JobDetails jobDetails
        if (config) {
            jobDetails = quartzService.getJobDetailsByTrigger(triggerName)
            jobDetails.config = config
            jobDetails.save()
        }

        if (jobDetails && jobDetails.errors.errorCount) {
            if (jobDetails.errors.getFieldError("config")) {
                Throwable exception = JobDetails.getConfigException(config)
                flash.alerts << "<pre>${ExceptionUtils.getStackTrace(exception)}</pre>"
                log.error "Tried to store a bad configuration", exception
            } else {
                flash.alerts << "Unknown exception trying to save configuration"
                log.error "Could not store job config: ${jobDetails.errors}"
            }

            def params = [errorConfig: config, id: triggerName]
            redirect(action: "show", params: params)
            return
        }

        quartzService.rescheduleJob(triggerName, availableSchedules)
        flash.messages << "made updates to job $triggerName" as String
        chain(action: LIST)
    }

    private static jobKey(name, group) {
        return JobKey.jobKey(name, group)
    }

    private Trigger searchForTrigger(String triggerName) {
        if (!triggerName) {
            flash.alerts << "Could not perform job operation, triggerName was not specified"
            chain(action: "index")
            return null
        }

        def triggerKey = new TriggerKey(triggerName)

        def trigger = quartzScheduler.getTrigger(triggerKey)
        if (!trigger) {
            flash.alerts << "Could not find trigger for trigger name ${triggerName}"
            redirect(action: "index")
            return null
        }

        return trigger
    }
}
