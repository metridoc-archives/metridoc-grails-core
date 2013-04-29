package metridoc.core

import grails.test.mixin.TestFor
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.quartz.Scheduler

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 4/1/13
 * Time: 10:30 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(InitQuartzService)
class InitQuartzServiceTests {

    String originalSystemProperty

    @Before
    void "grab original system property"() {
        originalSystemProperty = System.getProperty(InitQuartzService.CLI_ONLY)
    }

    @After
    void "reset original system property"() {
        setCliJobProperty(originalSystemProperty)
    }

    @Test
    void "scheduler should not start if it is a commandline job"() {
        setCliJobProperty("true")
        def startCalled = false
        service.quartzScheduler = [
                start: {
                    startCalled = true
                }
        ] as Scheduler
        service.startSchedulerIfNotACLIJob()
        assert !startCalled
        setCliJobProperty("false")
        service.startSchedulerIfNotACLIJob()
        assert startCalled
    }

    private void setCliJobProperty(String type) {
        System.setProperty(InitQuartzService.CLI_ONLY, type)
    }
}
