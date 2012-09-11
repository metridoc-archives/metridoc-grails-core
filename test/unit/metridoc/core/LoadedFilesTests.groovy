package metridoc.core



import grails.test.mixin.*
import org.junit.*
import org.springframework.validation.ObjectError

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(LoadedFiles)
class LoadedFilesTests {

    def _loadedFiles

    @Test
    void "test sha256 contraints"() {
        assert !loadedFiles.validate()
        assert "nullable" == loadedFiles.errors['sha256']

        loadedFiles.sha256 = "blah"
        assert !loadedFiles.validate()
        assert "size" == loadedFiles.errors['sha256']

        loadedFiles.sha256 = "12345678901234567890123456789012"
        assert !loadedFiles.validate()
        assert !loadedFiles.errors['sha256']
    }

    @Test
    void "test nullable constraints for documentContent and documentDate"() {
        loadedFiles.validate()
        assert !loadedFiles.errors["documentContent"]
        assert !loadedFiles.errors["documentDate"]
    }

    def getLoadedFiles() {
        if(_loadedFiles) return _loadedFiles

        _loadedFiles = new LoadedFiles()
        mockForConstraintsTests(LoadedFiles, [loadedFiles])
        return _loadedFiles
    }
}
