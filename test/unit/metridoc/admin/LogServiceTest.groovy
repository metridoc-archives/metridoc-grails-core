package metridoc.admin

import org.junit.Test

import static metridoc.admin.LogService.DATE_FORMAT

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
        def now = Date.parse(DATE_FORMAT,"2012-08-29 14:48:31")
        def htmlClazz = LogService.getDateClass("2012-08-29 12:48:31,548", null, now)
        assert "all sixHours twelveHours day" == htmlClazz
    }

    @Test
    void "render log between dates"() {
        def out = new ByteArrayOutputStream()
        long start = getTime("2012-08-29 14:48:31")
        LogService.renderLogStartingAt(out, sampleLog, start)
        def lines = []
        new ByteArrayInputStream(out.toByteArray()).eachLine {
            lines << it
        }

        assert 6 == lines.size()
        ["bar", "bam", "biz", "baz"].each {item ->
            assert 1 == lines.findAll {it.contains(item)}.size()
        }
    }

    private long getTime(String time) {
        Date.parse(DATE_FORMAT, time).time
    }

    def sampleLog = """
2012-08-29 13:48:31 foo
2012-08-29 14:48:31 bar
blah
blah
2012-08-29 14:50:31 bam
2012-08-29 14:55:31 biz
2012-08-29 15:48:31 baz
"""
}
