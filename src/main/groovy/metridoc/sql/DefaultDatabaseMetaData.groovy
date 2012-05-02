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
package metridoc.sql

import groovy.sql.Sql
import java.sql.SQLException
import javax.sql.DataSource
import metridoc.plugins.Plugin

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 10/4/11
 * Time: 12:46 PM
 */
@Plugin(category = "sqlMetaData", name = "default")
class DefaultDatabaseMetaData implements DatabaseMetaData {

    DataSource dataSource
    Sql sql

    Sql getSql() {
        if(sql) {
            return sql
        }

        sql = new Sql(dataSource)
    }

    List<Column> getColumns(String tableName) {
        def resultSet = sql.executeQuery(tableCheck(tableName))
        def metaData = resultSet.metaData
        def columnCount= metaData.columnCount
        def result = []

        (1..columnCount).each {
            def name = metaData.getColumnName(it)
            def type = metaData.getColumnTypeName(it)
            def size = metaData.getColumnDisplaySize(it)
            result.add(new Column(name: name, type: type, size: size))
        }

        return result
    }

    boolean tableExists(String tableName) {
        try {
            sql.execute(tableCheck(tableName))
            return true
        } catch (SQLException ex) {
            return false
        }
    }

    private static String tableCheck(String tableName) {
        return "select * from ${tableName} where 0 == 1"
    }
}
