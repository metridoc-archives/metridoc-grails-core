package metridoc.targets

import metridoc.dsl.JobBuilder
import org.junit.Before
import org.junit.Test
import org.apache.commons.lang.SystemUtils

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

        loader.config = new ConfigObject()

        loader.config.dataSource.username = "foo"
        loader.config.dataSource.password = "fooPassword"
        loader.config.dataSource.driverClassName = "foo.Driver"

        loader.config.dataSource_bar.username = "bar"
        loader.config.dataSource_bar.password = "fooPassword"
        loader.config.dataSource_bar.driverClassName = "foo.Driver"
        loader.grailsConsole = [
            info: {String message ->
                //do nothing, just a mock
            }
        ]

        loader.rootLoader = [
            addURL: {
                //do nothing, just a mock
            }
        ]
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
        def params = loader.extractDataSourceParameters("dataSource")
        assert params
        assert 4 == params.size()
        assert "foo" == params.username
        assert "fooPassword" == params.password
        assert "foo.Driver" == params.driverClassName

        assert !loader.extractDataSourceParameters("dataSource_foobar")
    }

    @Test
    void "test configuring a data Source through the target"() {
        int createDataSourceTimesCalled = 0
        def dataSourceListByUserName = ["foo", "bar"] as Set
        loader.createDataSource = {LinkedHashMap args ->
            createDataSourceTimesCalled++
            assert 4 == args.size()
            assert dataSourceListByUserName.remove(args.username)
        }

        loader.configureDataSources()
        assert 2 == createDataSourceTimesCalled
        assert 0 == dataSourceListByUserName.size()
    }

    @Test
    void "test loading drivers form grails directory" () {
        def driversDir = new File("${SystemUtils.USER_HOME}/.grails/drivers")
        def hasDrivers = driversDir.exists() && driversDir.isDirectory() && driversDir.listFiles().length > 0

        if(hasDrivers) {
            assert loader.getDatabaseDrivers().size() > 0
        }
    }
}
