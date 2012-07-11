package metridoc.targets

import metridoc.dsl.JobBuilder
import org.junit.Before
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/10/12
 * Time: 4:01 PM
 */
class _DataSourceLoaderTest {

    def loader

    @Before
    void "setup script"() {
        loader = new _DataSourceLoader()
        JobBuilder.isJob(loader)
        loader.run()
    }

    @Test
    void "test loading drivers"() {
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

    @Test
    void "test extracting data source parameters"() {
        loader.config = new ConfigObject()
        loader.config.dataSource.username = "foo"
        loader.config.dataSource.password = "fooPassword"
        loader.config.dataSource.driverClassName = "foo.Driver"

        def params = loader.extractDataSourceParameters("dataSource")
        assert params
        assert 3 == params.size()
        assert "foo" == params.username
        assert "fooPassword" == params.password
        assert "foo.Driver" == params.driverClassName

        assert !loader.extractDataSourceParameters("dataSource_bar")
    }
}
