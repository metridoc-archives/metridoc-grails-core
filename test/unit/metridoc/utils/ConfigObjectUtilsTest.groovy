package metridoc.utils

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 3/6/13
 * Time: 11:51 AM
 * To change this template use File | Settings | File Templates.
 */
class ConfigObjectUtilsTest {

    @Test
    void "make sure the cloned version doesn't override values from the original"() {
        def originalConfig = new ConfigObject()
        originalConfig.foo.bar = "baz"
        def cloned = ConfigObjectUtils.clone(originalConfig)
        def override = new ConfigObject()
        override.foo.bar = "notbaz"
        cloned.merge(override)
        assert "baz" == originalConfig.foo.bar
        assert 1 == originalConfig.size()
        assert 1 == cloned.size()
    }
}
