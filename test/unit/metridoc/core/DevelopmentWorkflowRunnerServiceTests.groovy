package metridoc.core



import grails.test.mixin.*
import org.junit.*
import grails.util.Environment

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(DevelopmentWorkflowRunnerService)
class DevelopmentWorkflowRunnerServiceTests {

    @Test
    void "server can only run during development or testing"() {
        assert service.canStartWorkflowServer(Environment.DEVELOPMENT)
        assert service.canStartWorkflowServer(Environment.TEST)
        assert !service.canStartWorkflowServer(Environment.PRODUCTION)
    }
}
