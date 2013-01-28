package metridoc.reports

import grails.test.mixin.Mock
import org.apache.commons.lang.StringUtils
import org.junit.Before
import org.junit.Test

import static metridoc.reports.ShiroUser.CONFIRM_MISMATCH
import static metridoc.reports.ShiroUser.FIELD_CANNOT_BE_NULL_OR_BLANK
import static metridoc.reports.ShiroUser.OLD_PASSWORD_MISMATCH
import static metridoc.reports.ShiroUser.PASSWORD_MISMATCH
import static metridoc.reports.ShiroUser.getEMAIL_ERROR
import static metridoc.reports.ShiroUser.getFIELD_CANNOT_BE_NULL_OR_BLANK
import static metridoc.reports.ShiroUser.getNOT_A_VALID_EMAIL

/**
 * See the API for {@link grails.test.mixin.support.GrailsUnitTestMixin} for usage instructions
 */
@Mock(ShiroUser)
class ShiroUserTests {

    static final blahEmailAddress = "blah@foo.com"
    def flash = [:]

    @Before
    void "prime the database"() {
        new ShiroUser(username:  "foobar", emailAddress:  blahEmailAddress, passwordHash: "blahlblah").save(flush: true, failOnError: true)
    }

    @Test
    void "test that flash messages are set on email errors"() {
        checkForError(passwordHash: "blah", username: "barfoo", emailAddress: blahEmailAddress, "emailAddress", EMAIL_ERROR(blahEmailAddress))
        checkForError(passwordHash: "blah", username: "barfoo", "emailAddress", FIELD_CANNOT_BE_NULL_OR_BLANK("email"))
        checkForError(passwordHash: "blah", username:  "barfoo", emailAddress:  StringUtils.EMPTY, "emailAddress", FIELD_CANNOT_BE_NULL_OR_BLANK("email"))
        checkForError(passwordHash: "blah", username:  "barfoo", emailAddress: "foobar", "emailAddress", NOT_A_VALID_EMAIL("foobar"))
    }

    @Test
    void "test that flash message are set on bad passwords"() {
        def fooEmail = "foo@foo.com"
        checkForError(validatePasswords: true,
                username: "barfoo",
                emailAddress: fooEmail,
                passwordHash: "blahblah",
                "passwordHash",
                FIELD_CANNOT_BE_NULL_OR_BLANK("password"))

        checkForError(validatePasswords: true,
                username: "barfoo",
                emailAddress: fooEmail,
                password:StringUtils.EMPTY,
                passwordHash: "blahblah",
                "passwordHash",
                FIELD_CANNOT_BE_NULL_OR_BLANK("password"))

        checkForError(validatePasswords: true,
                username: "barfoo",
                emailAddress: fooEmail,
                password:"bar",
                passwordHash: "blahblah",
                "passwordHash",
                PASSWORD_MISMATCH)

        checkForError(validatePasswords: true,
                oldPassword: "blahblah",
                username: "barfoo",
                emailAddress: fooEmail,
                password:"foobar",
                passwordHash: "blahblah",
                "passwordHash",
                OLD_PASSWORD_MISMATCH)

        checkForError(validatePasswords: true,
                username: "barfoo",
                emailAddress: fooEmail,
                password:"foobar",
                confirm: "barfoo",
                passwordHash: "blahblah",
                "passwordHash",
                CONFIRM_MISMATCH)
    }

    void checkForError(LinkedHashMap userParams, String field, String errorMessage) {
        Map flash = [:]
        ShiroUser user = new ShiroUser()
        userParams.each {
            user."${it.key}" = it.value
        }
        assert !user.validate()
        assert user.errors.getFieldError(field)
        ShiroUser.addAlertForAllErrors(user, flash)
        assert errorMessage == flash.alert
    }
}
