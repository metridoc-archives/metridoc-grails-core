package metridoc.auth

import grails.test.mixin.Mock
import metridoc.reports.ShiroRole
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/26/12
 * Time: 1:53 PM
 */
@Mock(ShiroRole)
class InitAuthServiceTest {

    @Test
    void "test creation of role name"() {
        assert "ROLE_FOO" == InitAuthService.createRoleName("foo")
    }

    @Test
    void "test role creation"() {
        ShiroRole role = InitAuthService.createRole("foo")
        assert "ROLE_FOO" == role.name
        assert role.permissions.contains("foo")
        assert 1 == role.permissions.size()
    }

    @Test
    void "test creation of default roles"() {
        def initAuthService = new InitAuthService()
        initAuthService.initDefaultRoles()

        assert 3 == ShiroRole.list().size()
    }
}
