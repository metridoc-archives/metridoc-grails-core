package metridoc.core



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(NotificationEmailsService)
class NotificationEmailsServiceTests {

    @Test
    void "whether mail is enable or not depends on the existence of the property grails_mail"() {
        assert !service.mailIsEnabled()
        enableMail()
        assert service.mailIsEnabled()
    }

    @Test
    void "mail is sent if mail is enabled, otherwise not"() {
        def mailHelper = new MailHelper()
        service.mailService = mailHelper
        service.doSendEmail(["foo"] as String[], "foo", "foo")
        assert !mailHelper.callCount
        enableMail()
        service.doSendEmail(["foo"] as String[], "foo", "foo")
        assert 1 == mailHelper.callCount
    }

    void enableMail() {
        def grailsApplication = [
                config : [
                        grails : [
                                mail: "mail mock"
                        ]
                ]
        ]
        service.grailsApplication = grailsApplication
    }

}

class MailHelper {
    int callCount = 0

    def sendMail(Closure closure) {
        closure.delegate = this
        callCount++
    }

    def to(recipients) {

    }

    def subject (String subject) {

    }

    def body (String body) {

    }
}