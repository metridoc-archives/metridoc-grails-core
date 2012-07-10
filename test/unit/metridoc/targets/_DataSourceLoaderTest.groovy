package metridoc.targets

import org.junit.Test
import metridoc.dsl.JobBuilder

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/10/12
 * Time: 4:01 PM
 */
class _DataSourceLoaderTest {

    @Test
    void "test loading drivers"() {
        def loader = new _DataSourceLoader()
        JobBuilder.isJob(loader)
        loader.run()
        loader.getDatabaseDrivers = {
            [new URL("http:foo"), new URL("http:bar")]
        }

        def urlCount = 0
        loader.rootLoader = [
            addURL: {
                urlCount++
            }
        ]

        loader.loadDrivers()
        assert 2 == urlCount
    }
}
