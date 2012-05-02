/*
 * Copyright 2010 Trustees of the University of Pennsylvania Licensed under the
 * Educational Community License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.osedu.org/licenses/ECL-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS"
 * BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package metridoc.utils

import metridoc.plugins.datasource.providers.CommonsDataSourceProvider
import metridoc.plugins.datasource.providers.H2DataSourceProvider
import metridoc.plugins.datasource.providers.MySqlDataSourceProvider

import javax.sql.DataSource

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/10/12
 * Time: 11:35 AM
 *
 * Helps create a {@link DataSource} without specifying a driver.  Also can create an embedded {@link DataSource} which
 * can be helpful for testing
 */
class DataSourceUtils {

    public static final EMBEDDED_DATA_SOURCE_URL = "jdbc:h2:mem:metridocInMemory"
    static Map dataSourceProviders = [h2: H2DataSourceProvider, mysql: MySqlDataSourceProvider]

    private static getDataSourceProvider(String url) {
        def m = (url =~ /jdbc:([^:]+)/)
        if (!m.lookingAt()) {
            throw new IllegalArgumentException("the passed jdbc url must be of the form jdbc:<provider>:<location>?<options>")
        }
        def providerName = m.group(1)
        def provider = dataSourceProviders[providerName]

        if (!provider) {
            provider = CommonsDataSourceProvider
        }

        return provider
    }

    static DataSource embeddedDataSource() {
        def url = EMBEDDED_DATA_SOURCE_URL
        getDataSourceProvider(url).getDataSource(url: url)
    }

    static DataSource getDataSource(Map args) {
        assert args.url || args.jdbcUrl: "A url must be provided to retrieve a datasource"
        DataSourceUtils.validateAndFormatArgs(args)
        def provider = getDataSourceProvider(args.url)

        return provider.getDataSource(args)
    }

    /**
     * standardizes all inputs
     *
     * @param args map of parameters to validate and standardize
     */
    public static void validateAndFormatArgs(Map args) {
        if (args.containsKey("jdbcUrl")) {
            args.url = args.remove("jdbcUrl")
        }
        if (args.containsKey("driverClass")) {
            args.driver = args.remove("driverClass")
        }
        if (args.containsKey("driverClassName")) {
            args.driver = args.remove("driverClassName")
        }
        if (args.containsKey("userName")) {
            args.user = args.remove("userName")
        }
    }

    private static void setProperties(LinkedHashMap args, DataSource dataSource) {
        args.each {key, value ->
            dataSource."${key}" = value
        }
    }
}
