package metridoc.core

import metridoc.core.QuartzMonitorJobFactory.QuartzDisplayJob
import org.apache.commons.lang.StringUtils
import org.quartz.Trigger

class JobRuns {

    Trigger.TriggerState triggerStatus
    Trigger trigger
    boolean manualJob
    Long start
    Long finish
    Boolean error = false
    JobDetails details
    byte[] jobLog

    static transients = ['triggerStatus', 'trigger', 'manualJob']

    static constraints = {
        jobLog(nullable: true, maxSize: 1024 * 1024 * 2)
    }

    static JobRuns createJobRun(QuartzDisplayJob displayJob) {
        def jobRun = new JobRuns()
        def triggerName = displayJob.trigger?.key?.name
        Assert.notNull(triggerName, "trigger name cannot be null")
        def jobDetails = JobDetails.findByJobName(triggerName)
        Assert.notNull(jobDetails, "Could not find details for job $triggerName")

        jobRun.with {
            start = displayJob.lastRun.time
            finish = displayJob.duration + start
            details = jobDetails
            def errorMessage = displayJob.error
            error = errorMessage != null && errorMessage != StringUtils.EMPTY
        }

        return jobRun
    }
}
