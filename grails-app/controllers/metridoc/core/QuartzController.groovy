package metridoc.core

import org.quartz.*
import org.quartz.impl.matchers.GroupMatcher

class QuartzController {

    def commonService

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

    def saveSettings() {
        redirect(action: "index")
    }


    def index() {
        redirect(action: "list")
    }

    def list() {
        def jobsList = []
        def listJobGroups = quartzScheduler.getJobGroupNames()
        listJobGroups?.each {jobGroup ->
            quartzScheduler.getJobKeys(GroupMatcher.groupEquals(jobGroup))?.each {jobKey ->
                def triggers = quartzScheduler.getTriggersOfJob(jobKey)
                if (triggers) {
                    triggers.each {Trigger trigger ->
                        def name = trigger.key.name
                        def unScheduled = false
                        def status = QuartzMonitorJobFactory.jobRuns[name]?.status ?: "complete"
                        def running = status != "running"

                        if (name.startsWith("manual") && trigger.previousFireTime && !running) {
                            def previousFireTime = trigger.previousFireTime.time
                            def now = new Date().time
                            if (now - previousFireTime > 1000 * 60 * 60 * 24) {
                                quartzScheduler.unScheduleJob(trigger.key)
                                unScheduled = true
                            }
                        }

                        if (!unScheduled) {
                            def currentJob = createJob(jobGroup, jobKey.name, jobsList, trigger.key.name)
                            currentJob.trigger = trigger
                            currentJob.triggerStatus = quartzScheduler.getTriggerState(trigger.key)
                        }
                    }
                } else {
                    createJob(jobGroup, jobKey.name, jobsList)
                }
            }
        }


        [jobs: jobsList, now: new Date(), scheduler: quartzScheduler, emailIsConfigured: commonService.emailIsConfigured()]
    }

    def start() {
        def trigger = triggers.get(params.jobName)
        quartzScheduler.scheduleJob(trigger)
        redirect(action: "list")
    }

    def pause() {
        quartzScheduler.pauseJob(jobKey(params.jobName, params.jobGroup))
        redirect(action: "list")
    }

    def resume() {
        quartzScheduler.resumeJob(jobKey(params.jobName, params.jobGroup))
        redirect(action: "list")
    }

    def runNow() {
        def dataMap = new JobDataMap(params)
        def triggerKey


        if (params.triggerName) {
            triggerKey = new TriggerKey(params.triggerName)

        } else {
            def jobKey = new JobKey(params.jobName, params.jobGroup)
            def triggers = quartzScheduler.getTriggersOfJob(jobKey)
            triggerKey = triggers[0].key
        }

        dataMap.oldTrigger = quartzScheduler.getTrigger(triggerKey)
        def now = new Date()

        def end = now + 365 * 5 //5 years in the future

        //next run will be 4 years in the future, clearly this won't run by then and the job will be unscheduled
        //the 5 year and 4 year number are arbitrary, basically we are tricking oracle to keep the job around so we can
        //continue to retrieve statistics from it
        def schedule = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1 * 24 * 56 * 4).repeatForever()
        def trigger = TriggerBuilder.newTrigger().forJob(params.jobName, params.jobGroup).startAt(new Date())
                .endAt(end).withIdentity(triggerKey).withSchedule(schedule).usingJobData(dataMap).build()

        quartzScheduler.rescheduleJob(triggerKey, trigger)
        Thread.sleep(1000) //sleep for 2 seconds to allow for the job to actually start


        if (params.containsKey("returnTriggerId")) {
            render trigger.key.name
        } else {
            redirect(action: "list")
        }
    }

    def status() {
        if (params.id) {
            def jobId = params.id
            def monitoredJobs = QuartzMonitorJobFactory.jobRuns
            render monitoredJobs[jobId]?.status ?: "unknown"
        } else {
            render(status: 400, text: "no id provided")
        }
    }

    def startScheduler() {
        quartzScheduler.start()
        redirect(action: "list")
    }

    def stopScheduler() {
        quartzScheduler.standby()
        redirect(action: "list")
    }

    private def createJob(String jobGroup, String jobName, ArrayList jobsList, triggerName = "") {
        def currentJob = [:]
        currentJob.group = jobGroup
        currentJob.name = jobName
        def map = QuartzMonitorJobFactory.jobRuns.get(triggerName)
        if (map) currentJob << map
        jobsList.add(currentJob)
        return currentJob
    }

    private jobKey(name, group) {
        return JobKey.jobKey(name, group)
    }

    private triggerKey(name, group) {
        return TriggerKey.triggerKey(name, group)
    }
}
