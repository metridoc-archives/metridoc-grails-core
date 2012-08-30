package metridoc.workflows

import org.junit.Test

import static metridoc.workflows.DefaultGrailsWorkflowClass.*

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 8/29/12
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
class DefaultGrailsWorkflowClassTest {

    @Test
    void "test time calculation"() {
        assert 1.32 == doCalculation(1321L, 1000)
    }

    @Test
    void "test formatting the time for duration"() {
        assert "100ms" == getPreviousDuration(100L);
        assert "1.0s" == getPreviousDuration(1000L);
        assert "1.32s" == getPreviousDuration(1320L);
        assert "1.0m" == getPreviousDuration(60000L);
        assert "1.0h" == getPreviousDuration(3600000L);
    }
}
