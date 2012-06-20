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
package metridoc.tools

import metridoc.plugins.sql.SqlPlus
import org.codehaus.gant.GantBinding

import javax.sql.DataSource

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 6/19/12
 * Time: 8:57 AM
 */
class DatabaseTool {

    Binding binding
    def dataSourceOrder = ["toDataSource", "dataSource", "fromDataSource"]
    private static final JDBC_PATTERN = /^jdbc:(\w+):.+$/

    DatabaseTool() {}

    DatabaseTool(GantBinding binding) {
        this.binding = binding
    }

    SqlPlus getSql() {
        def defaultDataSource = getDefaultDataSource()
        if (defaultDataSource) {
            return new SqlPlus(defaultDataSource)
        }

        return null
    }



    DataSource getDefaultDataSource() {

        def defaultDataSource
        dataSourceOrder.reverse().each {
            if (binding.variables.containsKey(it)) {
                defaultDataSource = binding."${it}"
                assert defaultDataSource instanceof DataSource: "${defaultDataSource} is not a dataSource"
            }
        }

        return defaultDataSource
    }

    /**
     * returns the database type based on convention.  If the binding contains any variables based on the
     * dataSourceOrder the database type will be returned, otherwise null is returned
     *
     * @return the database type, ie &quot;mysql&quot;
     */
    String getDatabaseType() {

        def defaultDataSource = getDefaultDataSource()

        if (defaultDataSource) {
            return getDatabaseType(defaultDataSource)
        } else {
            return null
        }
    }

    String getDatabaseType(DataSource dataSource) {
        getDatabaseType(dataSource.getConnection().getMetaData().getURL())
    }

    String getDatabaseType(String url) {
        def m = url =~ JDBC_PATTERN
        assert m.matches(): "${url} is not a valid jdbc url"
        return m.group(1)
    }
}
