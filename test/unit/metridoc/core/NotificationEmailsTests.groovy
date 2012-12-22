package metridoc.core



import grails.test.mixin.*
import org.apache.commons.lang.SystemUtils
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(NotificationEmails)
class NotificationEmailsTests {

    @Test
    void "given a string of emails separated by white space, they can be converted into a list"() {
        def emails = """
        foo@fam.com bar@blam.com
        foobar@bar.com
        """
        def emailList = NotificationEmails.convertEmailsToList(emails)
        assert "foo@fam.com" == emailList[0]
        assert "bar@blam.com" == emailList[1]
        assert "foobar@bar.com" == emailList[2]
    }

    @Test
    void "given a list of email notifications, emails can be converted to a string of new line delimited emails"() {
        def notifications = []
        notifications << new NotificationEmails(email: "foo@blam.com", scope: "foo")
        notifications << new NotificationEmails(email: "bar@blam.com", scope: "foo")
        notifications << new NotificationEmails(email: "baz@blam.com", scope: "foo")

        def newLine = SystemUtils.LINE_SEPARATOR
        assert "foo@blam.com${newLine}bar@blam.com${newLine}baz@blam.com${newLine}" as String ==
                NotificationEmails.convertListToString(notifications)
    }
}
