package metridoc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import metridoc.reports.ShiroUser
import org.apache.shiro.crypto.hash.Sha256Hash
import org.junit.Test

@TestFor(ProfileController)
@Mock(ShiroUser)
class ProfileControllerTests {

    ShiroUser user = new ShiroUser(emailAddress: "foo@bar.com", passwordHash: "123abc", username: "foo")

    @Test
    void "if changePW not checked then only the user email are changed"() {
        assert "foo@bar.com" == user.emailAddress
        ProfileController.updateUserInfo(user, [id:user.id, emailAddress: "bar@bar.com", username: "bar"]) //user name cannot change
        assert "bar@bar.com" == user.emailAddress
    }

    @Test
    void "if changePW is checked then user email and password are changed"() {
        assert "foo@bar.com" == user.emailAddress
        ProfileController.updateUserInfoAndPassword(user, [id:user.id, emailAddress: "bar@bar.com", username: "bar", password: "barPassword"]) //user name cannot change
        assert "bar@bar.com" == user.emailAddress
        assert new Sha256Hash("barPassword").toHex() == user.passwordHash
    }
}
