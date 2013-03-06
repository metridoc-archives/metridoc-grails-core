package metridoc.core

import grails.test.mixin.Mock
import grails.test.mixin.TestFor
import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/5/13
 * Time: 2:55 PM
 * To change this template use File | Settings | File Templates.
 */
@Mock(JobDetails)
class JobDetailsTest {

    def foo = new JobDetails(jobName: "foo")

    @Test
    void "if there is no config, then generate config will return an empty config"() {
        assert foo.generateConfigObject().isEmpty()
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
    void "config must either be a url or a valid config"() {
        foo.config = "http://blah.com"
        foo.validate()
        assert null == foo.errors.getFieldError("config")
        foo.config = "foo='bar';foobar=6"
        foo.validate()
        assert null == foo.errors.getFieldError("config")
        foo.config = "123kjahsdf(*&(*lkjasdfl"
        foo.validate()
        assert foo.errors.getFieldError("config")
        assert "invalid.config" == foo.errors.getFieldError("config").code
    }
}
