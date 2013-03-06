package metridoc.utils

import org.junit.Test
import org.quartz.Scheduler

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/6/13
 * Time: 10:13 AM
 * To change this template use File | Settings | File Templates.
 */
class QuartzUtilsTest {

    def quartzScheduler = [
            getTrigger: { null }
    ] as Scheduler

    @Test
    void "illegal argument exception thrown if trigger not found"() {
        use(QuartzUtils) {
            doIllegalArgumentCheck { quartzScheduler.triggerJobFromTriggerName("does not exist") }
        }
    }

    @Test
    void "illegal argument exception thrown if trigger is null"() {
        use(QuartzUtils) {
            doIllegalArgumentCheck { quartzScheduler.triggerJobFromTrigger(null) }
        }
    }

    @Test
    void "get trigger now throws illegal argument exception if trigger is null"() {
        doIllegalArgumentCheck { QuartzUtils.getTriggerNowTrigger(null) }
    }

    static void doIllegalArgumentCheck(Closure closure) {
        try {
            closure.call()
            assert false: "illegal argument should have occurred"
        } catch (IllegalArgumentException e) {
        }
    }
}
