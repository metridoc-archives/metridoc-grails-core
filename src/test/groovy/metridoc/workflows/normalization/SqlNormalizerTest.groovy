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
package metridoc.workflows.normalization

import javax.sql.DataSource
import metridoc.plugins.sql.SqlPlus
import metridoc.workflows.SqlNormalizer
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.Ignore

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/18/11
 * Time: 3:37 PM
 */
class SqlNormalizerTest extends Assert {

    DataSource dataSource
    SqlPlus sql

    @Before
    void createFoo() {
        dataSource = metridoc.utils.DataSourceUtils.embeddedDataSource()
        sql = sql()
        dropTables()
        sql.execute("create table foo (bar int, foo varchar(5), baz varchar(5))")
        sql.execute("create table blah (bar int, baz varchar(5))")

        sql.execute("insert into foo values('1', 'foo', 'baz')")
        sql.execute("insert into foo values('1', 'foo', 'baz')")
        sql.execute("insert into foo values('2','fool', 'baz21')")
        sql.execute("insert into blah values('2', 'baz22')")
    }

    @After
    void dropFoo() {
        dropTables()
        sql.close()
    }

    void dropTables() {
        sql.execute("drop table foo if exists")
        sql.execute("drop table blah if exists")
    }


    //TODO: un-ignore this once you have an h2 implementation
    @Ignore
    @Test
    void testSqlNormalizer() {
        def destination = [
            columns: ['bar', 'baz'],
            key: 'bar',
            table: 'blah'
        ];

        def sqlNormalizer = new SqlNormalizer(dataSource: dataSource, sourceTable: "foo", destination: destination);
        sqlNormalizer.run()

        assert 2 == sql.firstRow("select count(*) as total from blah").total
        assert 1 == sql.firstRow("select count(*) as total from blah where bar=2").total
    }



    int total(String tableName) {
        return firstRow("count(*) as total", tableName, "total")
    }

    def firstRow(String what, String from, String grab = what) {
        String firstRow = "select ${what} from ${from}"
        sql.firstRow(firstRow)."${grab}"
    }

    SqlPlus sql() {
        new SqlPlus(dataSource)
    }


}
