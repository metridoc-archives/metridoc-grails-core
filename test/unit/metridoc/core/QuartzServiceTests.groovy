package metridoc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import metridoc.trigger.Trigger
import org.junit.Test
import org.quartz.TriggerKey

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(QuartzService)
@Mock([JobSchedule, JobConfig])
class QuartzServiceTests {

    private static final String BAR_CONFIG = "http://bar.com"
    private static final String BAR = "bar"
    private static final String FOO = "foo"

    @Test
    void "fixing bug where null pointer exception occurs if an application starts but the jobs don't exist"() {
        new JobSchedule(triggerName: "foobarbazz", triggerType: Trigger.EVERY_10_MINUTES).save(flush: true)
        //this should not fail
        new QuartzService().initializeJobs()
    }

    @Test
    void "illegal argument exception thrown if trigger not found"() {
        def quartzScheduler = new Expando()
        quartzScheduler.getTrigger = { TriggerKey key -> null }
        service.quartzScheduler = quartzScheduler

        doIllegalArgumentCheck { service.triggerJobFromTriggerName("does not exist") }
    }

    @Test
    void "illegal argument exception thrown if trigger is null"() {
        doIllegalArgumentCheck { service.triggerJobFromTrigger(null) }
    }

    @Test
    void "get trigger now throws illegal argument exception if trigger is null"() {
        doIllegalArgumentCheck { service.getTriggerNowTrigger(null) }
    }

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

        assert QuartzService.isManual(manualTrigger)
        assert !QuartzService.isManual(nonManualTrigger)
    }

    @Test
    void "test building configuration for quartz job, app config overrides provided config"() {
        def jobConfig = new JobConfig()
        jobConfig.config = "foo=1;bar=2"
        jobConfig.triggerName = BAR
        jobConfig.save()

        def appConfig = new ConfigSlurper().parse("foo=2;foobar=10")

        def config = QuartzService.getConfigurationMergedWithAppConfig(appConfig, BAR)
        assert 2 == config.foo
        assert 2 == config.bar
        assert 10 == config.foobar
    }

    @Test
    void "test building and adding data to the binding variable"() {
        def appConfig = new ConfigSlurper().parse("foo=2;foobar=10")
        def binding = new Binding()

        QuartzService.addConfigToBinding(appConfig, binding)
        assert 2 == binding.foo
        assert 10 == binding.foobar
    }

    @Test
    void "getJobConfigByTriggerName returns a job config associated with the trigger, if it doesn't exist one is created"() {
        //check for creating one when one doesn't exist
        JobConfig jobConfig = service.getJobConfigByTrigger(FOO)
        assert jobConfig
        assert FOO == jobConfig.triggerName

        //check for the situation where it does exist
        JobConfig barConfig = new JobConfig(triggerName: BAR, config: BAR_CONFIG)
        barConfig.save()
        jobConfig = service.getJobConfigByTrigger(BAR)
        assert BAR_CONFIG == jobConfig.config
    }

    void doIllegalArgumentCheck(Closure closure) {
        try {
            closure.call()
            assert false: "illegal argument should have occurred"
        } catch (IllegalArgumentException e) {
        }
    }
}
