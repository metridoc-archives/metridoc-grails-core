package metridoc.core



import grails.test.mixin.*
import org.junit.*
import org.quartz.TriggerKey
import org.quartz.core.QuartzScheduler

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(QuartzService)
class QuartzServiceTests {

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
        doIllegalArgumentCheck {service.getTriggerNowTrigger(null)}
    }

    void doIllegalArgumentCheck(Closure closure) {
        try {
            closure.call()
            assert false: "illegal argument should have occurred"
        } catch (IllegalArgumentException e) {
        }
    }
}
