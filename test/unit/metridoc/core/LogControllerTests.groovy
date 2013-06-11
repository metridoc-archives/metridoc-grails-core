package metridoc.core

import grails.test.mixin.TestFor
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * Created with IntelliJ IDEA on 6/11/13
 * @author Tommy Barker
 */
@TestFor(LogController)
class LogControllerTests {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder()

    @Before
    void "add log files to temporary folder"() {
        folder.newFile("log1")
        folder.newFile("log2")
    }

    @SuppressWarnings("GroovyAccessibility")
    @Test
    void "basic testing of folder"() {
        def fileNames = controller.getLogFilesFrom(folder.root)
        assert 2 == fileNames.size()
        assert fileNames.contains("log1")
        assert fileNames.contains("log2")
    }
}
