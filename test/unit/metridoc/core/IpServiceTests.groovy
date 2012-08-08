package metridoc.core



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(IpService)
class IpServiceTests {

    def ipService = new IpService()

    @Test
    void "test ip identification"() {
        assert ipService.isIp("123.123.123.123")
        assert !ipService.isIp("foo")
    }
}
