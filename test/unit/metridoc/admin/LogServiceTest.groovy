package metridoc.admin

import org.junit.Test

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/30/12
 * Time: 9:38 AM
 * To change this template use File | Settings | File Templates.
 */
class LogServiceTest {

    @Test
    void "test date class rendering"() {
        def now = Date.parse(LogService.DATE_FORMAT,"2012-08-29 14:48:31")
        def htmlClazz = LogService.getDateClass("2012-08-29 12:48:31,548", null, now)
        assert "all sixHours twelveHours day" == htmlClazz
    }
}
