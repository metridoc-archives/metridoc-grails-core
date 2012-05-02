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
package metridoc.plugins.datasource.providers

import metridoc.utils.DataSourceUtils
import org.h2.jdbcx.JdbcConnectionPool
import org.h2.jdbcx.JdbcDataSource

import javax.sql.DataSource

class H2DataSourceProvider {
    static DataSource getDataSource(LinkedHashMap args) {
        DataSourceUtils.validateAndFormatArgs(args)
        def dataSource = new JdbcDataSource()

        if (args.password) {
            dataSource.password = args.remove(args.password)
        }

        if (args.user) {
            dataSource.user = args.remove(args.user)
        }

        dataSource.URL = args.remove(args.url)
        def pooledDataSource = new JdbcConnectionPool(dataSource)

        DataSourceUtils.setProperties(args, dataSource)

        return pooledDataSource
    }
}