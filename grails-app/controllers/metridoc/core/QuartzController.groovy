package metridoc.core

import grails.converters.JSON
import grails.web.RequestParameter
import org.apache.commons.lang.StringUtils
import org.apache.commons.lang.exception.ExceptionUtils
import org.apache.commons.lang.text.StrBuilder
import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher

/**
 * Manages the installed jobs in metridoc
 */
class QuartzController {

    private static final String LIST = "list"
    def commonService
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

    //TODO: reduce the size of this method, this is getting out of hand
    def list() {
        def jobsList = []
        def listJobGroups = quartzScheduler.getJobGroupNames()

        listJobGroups?.each { jobGroup ->
            quartzScheduler.getJobKeys(GroupMatcher.groupEquals(jobGroup))?.each { jobKey ->
                def triggers = quartzScheduler.getTriggersOfJob(jobKey)
                if (triggers) {
                    triggers.each { org.quartz.Trigger trigger ->
                        def currentJob = createJob(jobGroup, jobKey.name, jobsList, trigger.key.name)
                        currentJob.trigger = trigger
                        currentJob.manualJob = QuartzService.isManual(trigger)
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
        if (params.badEmails) {
            def badEmailMessageBuilder = new StrBuilder("The following emails are not valid: ")
            if (params.badEmails instanceof String) {
                badEmailMessageBuilder.append(params.badEmails)
                badEmailMessage = badEmailMessageBuilder.toString()
            } else {
                params.badEmails.each {
                    badEmailMessageBuilder.append(it)
                    badEmailMessageBuilder.append(", ")
                }
                badEmailMessage = badEmailMessageBuilder.toString()
                badEmailMessage = StringUtils.chop(badEmailMessage)
                badEmailMessage = StringUtils.chop(badEmailMessage)
            }
        }

        if (badEmailMessage) {
            flash.alerts << badEmailMessage
        }

        if (NotificationEmails.count() == 0) {
            flash.alerts << "No emails have been set, no one will be notified of job failures"
        }
        if (!commonService.emailIsConfigured()) {
            flash.alerts << "Email has not been set up properly, no notifications will be sent on job failures"
        }
        return [
                jobs: jobsList,
                now: new Date(),
                scheduler: quartzScheduler,
                notificationEmails: notificationEmails,
        ]
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
            displayJob.interrupting = true;
        } catch (UnableToInterruptJobException e) {
            flash.alert = e.message
        }
        displayJob.interrupting = true
        redirect(action: LIST)
    }

    def show(@RequestParameter("id") String triggerName, String errorConfig) {
        org.quartz.Trigger trigger = searchForTrigger(triggerName)
        def jobSchedule = JobSchedule.findByTriggerName(triggerName)
        def currentSchedule = jobSchedule ? jobSchedule.triggerType.toString() : "DEFAULT"
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
            def jobConfig = JobConfig.findByTriggerName(triggerName)
            config = jobConfig ? jobConfig.config : null
        }
        if (!trigger) return

        [
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

        JobConfig jobConfig
        if (config) {
            jobConfig = quartzService.getJobConfigByTrigger(triggerName)
            jobConfig.config = config
            jobConfig.save()
        }

        if (jobConfig && jobConfig.errors.errorCount) {
            if (jobConfig.errors.getFieldError("config")) {
                Throwable exception = JobConfig.getConfigException(config)
                flash.alerts << "<pre>${ExceptionUtils.getStackTrace(exception)}</pre>"
                log.error "Tried to store a bad configuration", exception
            } else {
                flash.alerts << "Unknown exception trying to save configuration"
                log.error "Could not store job config: ${jobConfig.errors}"
            }

            def params = [errorConfig: config, id: triggerName]
            redirect(action: "show", params: params)
            return
        }

        quartzService.rescheduleJob(triggerName, availableSchedules)
        flash.messages << "made updates to job $triggerName" as String
        chain(action: LIST)
    }

    private static QuartzMonitorJobFactory.QuartzDisplayJob createJob(String jobGroup, String jobName, List<QuartzMonitorJobFactory.QuartzDisplayJob> jobsList, triggerName = "") {
        def displayJob = QuartzMonitorJobFactory.jobRuns.get(triggerName)
        if (!displayJob) {
            displayJob = new QuartzMonitorJobFactory.QuartzDisplayJob()
            displayJob.setJobKey(new JobKey(jobName, jobGroup))
        }

        jobsList.add(displayJob)
        return displayJob
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
