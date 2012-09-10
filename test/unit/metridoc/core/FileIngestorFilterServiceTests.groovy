package metridoc.core



import grails.test.mixin.*
import org.junit.*

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(FileIngestorFilterService)
class FileIngestorFilterServiceTests {

    @Test
    void "retrieve filters by name with config object"() {
        def config = new ConfigObject()
        config.metridoc.files.foo.filter = {

        }
        def filtersByName = service.getFiltersByName(config)
        assert filtersByName.foo
        assert filtersByName.foo instanceof Closure
    }
}
