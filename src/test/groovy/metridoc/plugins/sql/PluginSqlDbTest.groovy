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

import org.junit.Test
import java.sql.DatabaseMetaData
import java.sql.Connection
import javax.sql.DataSource
import metridoc.sql.DefaultBulkSqlCalls;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 10/19/11
 * Time: 3:03 PM
 */
public class PluginSqlDbTest {

    @Test
    void canExtractDbTypeFromUrl() {
        def url = "jdbc:foo://bar"
        assert "foo" == PluginSqlDb.getDatabaseType(url)
    }

    @Test(expected = IllegalArgumentException.class)
    void exceptionThrownIfNotAJdbcUrl() {
        def url = "junk:foo://bar"
        PluginSqlDb.getDatabaseType(url)
    }

    @Test
    void testEndToEndForDbTypeFromDataSource() {
        def metaData = [
                getURL: {"jdbc:foo://bar"}
        ] as DatabaseMetaData

        def connection = [
                getMetaData: {metaData}
        ] as Connection

        def dataSource = [
                getConnection: {connection}
        ] as DataSource

        assert "foo" == PluginSqlDb.getDatabaseType(dataSource)
    }

    @Test
    void testThatDefaultChosenIfThereAreNoImplementationsForDatabase() {
        assert PluginSqlDb.getBulkCalls("foo") instanceof DefaultBulkSqlCalls
    }
}
