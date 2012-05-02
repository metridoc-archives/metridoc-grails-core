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
package metridoc.plugins.sql

import java.sql.Connection
import javax.sql.DataSource
import metridoc.utils.Assert
import metridoc.sql.BulkSqlCalls
import metridoc.plugins.PluginDB

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 10/19/11
 * Time: 3:02 PM
 */
class PluginSqlDb {

    private static final String BULK_SQL = "bulkSql"

    /**
     * should never be instantiated
     */
    private PluginSqlDb() {}

    static BulkSqlCalls getBulkCalls(String dbms) {
        def plugin = PluginDB.getInstance().getPlugin(BULK_SQL, dbms)
        if(plugin) {
            return plugin.newInstance()
        }

        PluginDB.getInstance().getPlugin(BULK_SQL, "default").newInstance()
    }

    static BulkSqlCalls getBulkCalls(Connection connection) {
        getBulkCalls(getDatabaseType(connection))
    }

    static BulkSqlCalls getBulkCalls(DataSource dataSource) {
        getBulkCalls(getDatabaseType(dataSource))
    }

    private static String getDatabaseType(String jdbcUrl) {
        def m = jdbcUrl =~ /jdbc:([^:]+)/
        Assert.isTrue(m.lookingAt(), "${jdbcUrl} is not a jdbc url")
        m.group(1)
    }

    private static String getDatabaseType(DataSource dataSource) {
        getDatabaseType(dataSource.connection)
    }

    private static String getDatabaseType(Connection connection) {
        getDatabaseType(connection.metaData.URL)
    }
}
