package metridoc.core

import grails.test.mixin.Mock
import org.apache.commons.lang.StringUtils
import org.apache.shiro.crypto.hash.Sha256Hash
import org.junit.Before
import org.junit.Test

import static metridoc.core.ShiroUser.*

@Mock([ShiroUser, ShiroRole])
class ShiroUserTests {

    static final blahEmailAddress = "blah@foo.com"
    def flash = [:]

    @Before
    void "prime the database"() {
        def adminRole = new ShiroRole(name: "ROLE_ADMIN")
        adminRole.save(flush: true)
        new ShiroUser(username: "foobar", emailAddress: blahEmailAddress, passwordHash: "blahlblah").save(flush: true, failOnError: true)
        new ShiroUser(username: "admin", emailAddress: "admin@admin.com", passwordHash: "blahlblah", roles: [adminRole]).save(flush: true)
    }

    @Test
    void "user admin must always have a role admin"() {
        def admin = ShiroUser.findByUsername("admin")
        admin.roles = []
        assert !admin.validate()
        addAlertForAllErrors(admin, flash)
        assert ADMIN_MUST_HAVE_ROLE_ADMIN == flash.alert
    }

    @Test
    void "test that flash messages are set on email errors"() {
        checkForError(passwordHash: "blah", username: "barfoo", emailAddress: blahEmailAddress, "emailAddress", EMAIL_ERROR(blahEmailAddress))
        checkForError(passwordHash: "blah", username: "barfoo", "emailAddress", FIELD_CANNOT_BE_NULL_OR_BLANK("email"))
        checkForError(passwordHash: "blah", username: "barfoo", emailAddress: StringUtils.EMPTY, "emailAddress", FIELD_CANNOT_BE_NULL_OR_BLANK("email"))
        checkForError(passwordHash: "blah", username: "barfoo", emailAddress: "foobar", "emailAddress", NOT_A_VALID_EMAIL("foobar"))
    }

    @Test
    void "test flash messages for username errors"() {
        def fooEmail = "foo@foo.com"
        checkForError(
                emailAddress: fooEmail,
                passwordHash: "blahblah",
                "username",
                FIELD_CANNOT_BE_NULL_OR_BLANK("username")
        )

        checkForError(
                username: StringUtils.EMPTY,
                emailAddress: fooEmail,
                passwordHash: "blahblah",
                "username",
                FIELD_CANNOT_BE_NULL_OR_BLANK("username")
        )

        checkForError(
                username: "foobar",
                emailAddress: fooEmail,
                passwordHash: "blahblah",
                "username",
                USERNAME_IS_NOT_UNIQUE
        )
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
                password: StringUtils.EMPTY,
                passwordHash: "blahblah",
                "passwordHash",
                FIELD_CANNOT_BE_NULL_OR_BLANK("password"))

        checkForError(validatePasswords: true,
                username: "barfoo",
                emailAddress: fooEmail,
                password: "bar",
                passwordHash: "blahblah",
                "passwordHash",
                PASSWORD_MISMATCH)

        checkForError(validatePasswords: true,
                oldPassword: "blahblah",
                username: "barfoo",
                emailAddress: fooEmail,
                password: "foobar",
                passwordHash: "blahblah",
                "passwordHash",
                OLD_PASSWORD_MISMATCH)

        checkForError(validatePasswords: true,
                oldPassword: "blahblah",
                username: "barfoo",
                emailAddress: fooEmail,
                password: "foobar",
                confirm: "barfoo",
                passwordHash: new Sha256Hash("blahblah").toHex(),
                "passwordHash",
                CONFIRM_MISMATCH)
    }

    static void checkForError(LinkedHashMap userParams, String field, String errorMessage) {
        Map flash = [:]
        ShiroUser user = new ShiroUser()
        userParams.each {
            user."${it.key}" = it.value
        }
        assert !user.validate()
        assert user.errors.getFieldError(field)
        addAlertForAllErrors(user, flash)
        assert errorMessage == flash.alert
    }
}
