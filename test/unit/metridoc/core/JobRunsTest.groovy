package metridoc.core

import grails.test.mixin.Mock
import metridoc.utils.JobTrigger
import org.junit.Test
import org.quartz.SimpleTrigger
import org.quartz.TriggerKey

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/5/13
 * Time: 1:40 PM
 * To change this template use File | Settings | File Templates.
 */
@Mock([JobDetails, JobRuns])
class JobRunsTest {

    @Test
    void "create a job run from a display job"() {
        def jobDetails = new JobDetails().with {
            jobName = "foo"
            template = "bar"
            jobTrigger = JobTrigger.NEVER
            save(failOnError: true)
            return it
        }
        def displayJob = new QuartzMonitorJobFactory.QuartzDisplayJob()
        def start = Date.parse("yyyyMMdd", "20120101")
        def duration = start.time + 2000
        displayJob.lastRun = start
        displayJob.duration = duration
        displayJob.trigger = jobDetails.convertTriggerToQuartzTrigger()
        displayJob.trigger.key = new TriggerKey("foo")

        def jobRun = JobRuns.createJobRun(displayJob)

        assert start.time == jobRun.start
        assert start.time + duration == jobRun.finish
        assert jobRun.details
        assert jobRun.validate()
    }
}
