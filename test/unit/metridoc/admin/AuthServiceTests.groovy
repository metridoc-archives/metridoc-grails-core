package metridoc.admin



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(AuthService)
class AuthServiceTests {

    @Test
    void "do basic id check"() {
        def id = service.addResetLink()
        assert service.canReset(id)
    }

    @Test
    void "id should be removed after checked"() {
        def id = service.addResetLink()
        assert service.canReset(id)
        assert service.idByDate.size() == 0
    }

    @Test
    void "test that ids expire" () {
        def id = service.addResetLink()
        def twentyMinutes = 1000 * 60 * 20
        def now = new Date().time + twentyMinutes
        assert !service.canReset(id, now)
    }

    @Test
    void "cant reset if the id is not in the cache" () {
        assert !service.canReset(123)
    }
}
