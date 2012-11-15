package metridoc.admin

import metridoc.core.QuartzMonitorJobFactory
import org.quartz.JobKey
import org.quartz.Trigger
import org.quartz.TriggerKey
import org.quartz.impl.matchers.GroupMatcher
import org.quartz.JobDataMap
import org.quartz.TriggerBuilder
import org.apache.commons.lang.math.RandomUtils
import org.quartz.ScheduleBuilder
import org.quartz.SimpleScheduleBuilder

class QuartzController {
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
                        if (name.startsWith("manual") && trigger.previousFireTime) {
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
        [jobs: jobsList, now: new Date(), scheduler: quartzScheduler]
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
        def now = new Date()

        def end = now + 365 * 5 //5 years in the future

        def schedule = SimpleScheduleBuilder.simpleSchedule().withIntervalInHours(1 * 24 * 56).repeatForever()
        def triggerId = "manualRun-${RandomUtils.nextInt(1000)}"
        def trigger = TriggerBuilder.newTrigger().forJob(params.jobName, params.jobGroup).startAt(new Date())
                .endAt(end).withIdentity(triggerId).withSchedule(schedule).build()
        quartzScheduler.scheduleJob(trigger)

        if (params.containsKey("returnTriggerId")) {
            render triggerId
        } else {
            redirect(action: "list")
        }
    }

    def status() {
        render quartzScheduler.getTriggerState(TriggerKey.triggerKey(params.id))
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
