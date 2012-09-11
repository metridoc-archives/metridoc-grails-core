package metridoc.core



import grails.test.mixin.*
import org.junit.*
import org.apache.camel.component.file.GenericFile
import org.apache.shiro.crypto.hash.Sha256Hash

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(FileIngestorFilterService)
class FileIngestorFilterServiceTests {

    def config = new ConfigObject()
    def fooFile = new GenericFile()
    def barFile = new GenericFile()
    def fooFilter
    def genericFile = new GenericFile()
    def sha256

    @Before
    void "setup config and file filter"() {
        config.metridoc.files.foo.filter = {
            it.fileNameOnly.contains("foo")
        }

        service.grailsApplication = [
            config: config
        ]

        fooFile.setFileNameOnly("foobar")
        barFile.setFileNameOnly("bar")
        fooFilter = config.metridoc.files.foo.filter
        byte[] bytes = "foo".bytes
        sha256 = new Sha256Hash(bytes).toHex()
        def file = File.createTempFile("someFile", null)
        file.deleteOnExit()
        file.write("foo")
        genericFile.file = file
    }

    @Test
    void "test byte checking"() {
        assert service.compareBytes(genericFile, sha256)
    }

    @Test
    void "retrieve filters by name"() {
        def filtersByName = service.filtersByName
        assert filtersByName.foo
        assert filtersByName.foo instanceof Closure
    }

    @Test
    void "retrieve filters by name with config object"() {
        def filtersByName = service.getFiltersByName(config)
        assert filtersByName.foo
        assert filtersByName.foo instanceof Closure
    }

    @Test
    void "check that a closure that is meant to filter a file does"() {
        assert service.filterFile(fooFile, fooFilter)
        assert !service.filterFile(barFile, fooFilter)
    }

    @Test
    void "check that filtering works from instantiation"() {
        assert service.acceptFromFilter(fooFile)
        assert !service.acceptFromFilter(barFile)
    }
}
