package metridoc.utils

import org.junit.Test
import org.apache.commons.lang.SystemUtils

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 12/4/12
 * Time: 1:42 PM
 * To change this template use File | Settings | File Templates.
 */
class ShiroBootupUtilsTest {

    @Test
    void "test adding rest info at the end"() {

        def newLine = SystemUtils.LINE_SEPARATOR
        def rest = ShiroBootupUtils.addRestDefinitions("/blah = user,roles[SOME_ROLE]")
        def expected = "/blah = user,roles[SOME_ROLE]${newLine}/rest/blah = authcBasic,roles[SOME_ROLE]${newLine}" as String
        assert rest == expected
    }
}
