import grails.test.mixin.TestFor
import metridoc.core.AuthController
import org.junit.Test
/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 10/26/12
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */
@TestFor(AuthController)
class AuthControllerTests {

    @Test
    void "forgotPassword in model is not there if mail is not set up correctly"() {
        def grailsApplication = [
                config: [
                        grails: [
                                mail: [
                                        username: null
                                ]
                        ]
                ]
        ]
        controller.grailsApplication = grailsApplication
        def model = controller.index()
        assert !model.containsKey("forgotPassword")
    }

    @Test
    void "forgotPassword is available if mail is set"() {
        //TODO: it just checks if mail is anything but null or false, maybe we should do something a little more
        //elegant to check if it is set correctly?
        def grailsApplication = [
                config: [
                        grails: [
                                mail: [
                                        username: "foo"
                                ]
                        ]
                ]
        ]

        controller.grailsApplication = grailsApplication
        assert controller.grailsApplication.config.grails.mail.username
        def model = controller.index()
        assert model.forgotPassword
        controller.grailsApplication = false
    }
}
