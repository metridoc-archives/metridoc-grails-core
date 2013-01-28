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
        ShiroUser.withTransaction{
            def adminUser = new ShiroUser(username: "admin", passwordHash: "asdasd", emailAddress: "foo@foo.com")
            def adminRole = new ShiroRole(name: "ROLE_ADMIN")

            adminUser.addToRoles(adminRole)
            assert adminUser.save()

            def fooUser = new ShiroUser(username: "foo", passwordHash: "asdasd", emailAddress: "foo@bar.com")
            def fooRole = new ShiroRole(name: "ROLE_FOO")

            fooUser.addToRoles(fooRole)
            assert fooUser.save()

            assert new ShiroRole(name: "ROLE_BAR").save()

            assert 2 == ShiroUser.list().size()
            assert 3 == ShiroRole.list().size()
        }
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
