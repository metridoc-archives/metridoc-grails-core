package metridoc.admin

import grails.test.mixin.Mock
import metridoc.reports.ShiroRole
import metridoc.reports.ShiroUser
import org.junit.Before
import org.junit.Ignore
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/6/12
 * Time: 3:13 PM
 */
@Mock([ShiroRole, ShiroUser])
class UserServiceTest {

    def userService = new UserService()

    @Before
    void setupMockData() {
        def anonymous = new ShiroRole(name: UserService.ROLE_ANONYMOUS)
        anonymous.id = 1
        anonymous.save(flush: true)
        def foo = new ShiroRole(name: "ROLE_FOO")
        foo.id = 2
        foo.save(flush: true)
    }

    @Test
    void testAddingRoles() {
        def user = new ShiroUser()
        userService.addRolesToUser(user, ["ROLE_FOO"])
        assert 2 == user.roles.size()
        user.roles.each {
            assert "ROLE_FOO" == it.name || "ROLE_ANONYMOUS" == it.name
        }
    }

    @Test
    void "anonymous is added by default regardless of whether or not any roles are provided"() {
        def user = new ShiroUser()
        userService.addRolesToUser(user, null)
        assert 1 == user.roles.size()

        user.roles.each {
            assert UserService.ROLE_ANONYMOUS == it.name
        }
    }
}
