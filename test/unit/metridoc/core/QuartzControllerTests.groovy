package metridoc.core

import grails.test.mixin.TestFor
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/6/13
 * Time: 10:49 AM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(QuartzController)
class QuartzControllerTests {

    @Test
    void "testing error chain"() {
        controller.flash.alerts = []
        controller.errorChain("some error")
        def alerts = controller.flash.alerts
        assert 1 == alerts.size()
        assert "some error" == alerts.get(0)
    }
}
