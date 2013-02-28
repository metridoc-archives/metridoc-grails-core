package metridoc.core

import grails.test.mixin.TestFor
import org.junit.Test

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */
@TestFor(JobConfig)
class JobConfigTests {

    def foo = new JobConfig(triggerName: "foo")

    @Test
    void "if there is no config, then generate config will return null"() {
        assert null == foo.generateConfigObject()
    }

    @Test
    void "if there is a String config, generate config will create it"() {
        foo.config = """
            foo {
                bar = 5
            }
        """
        assert 5 == foo.generateConfigObject().foo.bar
    }

    @Test
    void "test configIsUrl"() {
        foo.config = "bar = 5"
        assert !foo.configIsAUrl()
        foo.config = "https://blah.com"
        assert foo.configIsAUrl()
    }

    @Test
    void "if config is a url, then the file is parsed"() {
        def file = File.createTempFile("foo", "groovy")
        file.deleteOnExit()
        file << """
            foo {
                bar = 5
            }
        """
        foo.config = file.toURI().toURL().toString()
        assert 5 == foo.generateConfigObject().foo.bar
    }

    @Test
    void "trigger name cannot be null"() {
        assert !new JobConfig().validate()
        assert foo.validate()
    }

    @Test
    void "config must either be a url or a valid config"() {
        foo.config = "http://blah.com"
        assert foo.validate()
        foo.config = "foo='bar';foobar=6"
        assert foo.validate()
        foo.config = "123kjahsdf(*&(*lkjasdfl"
        assert !foo.validate()
        assert "invalid.config" == foo.errors.getFieldError("config").code
    }
}
