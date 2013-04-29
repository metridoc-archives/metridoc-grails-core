package metridoc.core

import grails.util.Environment
import org.junit.Test
import org.quartz.Scheduler

/**
 * Created with IntelliJ IDEA on 4/29/13
 * @author Tommy Barker
 */
class InitQuartzServiceIntTests {
    Scheduler quartzScheduler

    @Test
    void "make sure the quartz scheduler is not started in the testing environment"() {
//        assert Environment.current == Environment.TEST : "make sure to run integrated tests with [grails test tA :integration]"
        assert !quartzScheduler.isStarted()
    }

}
