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

    @Test
    void "illegal argument exception thrown if trigger not found"() {
        def quartzScheduler = [
                getTrigger:{null}
        ] as Scheduler

        use(QuartzUtils) {
            doIllegalArgumentCheck {quartzScheduler.triggerJobFromTriggerName("does not exist")}
        }
    }

    void doIllegalArgumentCheck(Closure closure) {
        try {
            closure.call()
            assert false: "illegal argument should have occurred"
        } catch (IllegalArgumentException e) {
        }
    }
}
