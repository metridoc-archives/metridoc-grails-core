package metridoc.admin

import metridoc.core.QuartzMonitorJobFactory
import org.quartz.JobKey
import org.quartz.Trigger
import org.quartz.TriggerKey
import org.quartz.impl.matchers.GroupMatcher

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
                        def currentJob = createJob(jobGroup, jobKey.name, jobsList, trigger.key.name)
                        currentJob.trigger = trigger
                        currentJob.triggerStatus = quartzScheduler.getTriggerState(trigger.key)
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
        quartzScheduler.triggerJob(jobKey(params.jobName, params.jobGroup), null)
        redirect(action: "list")
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
