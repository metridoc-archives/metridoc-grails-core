package metridoc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import metridoc.utils.JobTrigger
import metridoc.utils.QuartzUtils
import org.junit.Test

import static metridoc.utils.JobTrigger.NEVER

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(QuartzService)
@Mock([JobDetails])
class QuartzServiceTests {

    private static final String BAR_CONFIG = "http://bar.com"
    private static final String BAR = "bar"
    private static final String FOO = "foo"

    @Test
    void "test trigger check for is manual"() {
        def manualTrigger = [
                getNextFireTime: {
                    long now = new Date().time
                    long threeYearsIncrement = 1000L * 60L * 60L * 24L * 365L * 3L
                    Date threeYears = new Date(now + threeYearsIncrement)
                    return threeYears
                }
        ] as org.quartz.Trigger

        def nonManualTrigger = [
                getNextFireTime: {
                    new Date()
                }
        ] as org.quartz.Trigger

        assert QuartzUtils.isManual(manualTrigger)
        assert !QuartzUtils.isManual(nonManualTrigger)
    }

    @Test
    void "test building configuration for quartz job, provided config overrides app config"() {
        def jobDetails = new JobDetails()

        jobDetails.with {
            config = "foo=1;bar=2"
            jobName = BAR
            template = BAR
            jobTrigger = NEVER
            save(failOnError: true)
        }

        def appConfig = new ConfigSlurper().parse("foo=2;foobar=10")

        def config = QuartzService.getConfigurationMergedWithAppConfig(appConfig, BAR)
        assert 1 == config.foo
        assert 2 == config.bar
        assert 10 == config.foobar
    }

    @Test
    void "test building and adding data to the binding variable"() {
        def appConfig = new ConfigSlurper().parse("foo=2;foobar=10")
        def binding = new Binding()

        QuartzService.addConfigToBinding(appConfig, binding)
        assert 2 == binding.config.foo
        assert 10 == binding.config.foobar
    }

    @Test
    void "getJobConfigByTriggerName returns a job config associated with the trigger, if it doesn't exist one is created"() {
        //check for creating one when one doesn't exist
        JobDetails jobDetails = service.getJobDetailsByTrigger(FOO)
        assert jobDetails
        assert FOO == jobDetails.jobName

        //check for the situation where it does exist
        JobDetails barDetails = new JobDetails(
                jobName: BAR,
                config: BAR_CONFIG,
                template: BAR,
                jobTrigger: JobTrigger.NEVER,
        )
        barDetails.save(failOnError: true)
        jobDetails = service.getJobDetailsByTrigger(BAR)
        assert BAR_CONFIG == jobDetails.config
    }
}
