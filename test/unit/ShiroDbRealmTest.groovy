import metridoc.reports.ShiroUser
import grails.test.mixin.TestFor
import org.junit.Test
import metridoc.reports.ShiroRole
import grails.test.mixin.Mock
import grails.test.mixin.domain.DomainClassUnitTestMixin
import grails.test.mixin.TestMixin
import org.junit.Before
/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 10/25/12
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
@Mock([ShiroUser, ShiroRole])
class ShiroDbRealmTest {

    def realm = new ShiroDbRealm()

    @Before
    void "create some data"() {
        def user = new ShiroUser(username: "admin", passwordHash: "asdasd")
        def role = new ShiroRole(name: "ROLE_ADMIN")

        user.addToRoles(role)
        user.save(failOnError: true)

        user = new ShiroUser(username: "foo", passwordHash: "asdasd")
        role = new ShiroRole(name: "ROLE_FOO")

        user.addToRoles(role)
        user.save(failOnError: true)

        new ShiroRole(name: "ROLE_BAR").save(failOnError: true)
    }

    @Test
    void "if a user has the ROLE_ADMIN role, then they have all roles"() {
        assert realm.hasRole("admin", "ROLE_FOO")
        def roles = ShiroRole.findAllByNameInList(["ROLE_FOO", "ROLE_BAR"])
        assert roles.size() == 2
        assert realm.hasAllRoles("admin", roles)
    }

    @Test
    void "basic check if a user is an admin within the shirodbrealm" () {
        assert realm.isAdmin("admin")
    }

}
