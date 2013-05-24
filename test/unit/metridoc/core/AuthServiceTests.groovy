package metridoc.core

import grails.test.mixin.TestFor
import org.junit.Test

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
        assert service.dateById.size() == 0
    }

    @Test
    void "test that ids expire"() {
        def id = service.addResetLink()
        def twentyMinutes = 1000 * 60 * 20
        def now = new Date().time + twentyMinutes
        //noinspection GroovyAccessibility
        assert !service.canDoReset(id, now)
    }

    @Test
    void "cant reset if the id is not in the cache"() {
        assert !service.canReset(123)
    }
}
