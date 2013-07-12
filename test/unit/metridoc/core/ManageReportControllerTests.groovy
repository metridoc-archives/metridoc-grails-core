package metridoc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor

/**
 * Created with IntelliJ IDEA on 4/15/13
 * @author Tommy Barker
 */
@TestFor(ManageReportController)
@Mock(ManageReport)
class ManageReportControllerTests {

    void "test fixing bug where NPE occurs when setting permissions for a controller and the controller has no roles"() {
        new ManageReport(controllerName: "foo").save(failOnError: true)
        controller.initAuthService = [
                init: {
                    //do nothing, just mock it so unit test will run
                }
        ]
        controller.manageReportService = new ManageReportService()
        controller.manageReportService.initAuthService = [
                init: {

                }
        ]
        controller.update("foo") //null pointer exception will occur here if bug exists
    }
}
