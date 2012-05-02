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
package metridoc.camel.component.sqlplus

import metridoc.dsl.JobBuilder
import metridoc.plugins.sql.SqlPlus
import org.apache.camel.CamelExecutionException
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/7/11
 * Time: 5:42 PM
 */
public class SqlPlusComponentRoutingTest {

    SqlPlus sqlPlus
    def job = JobBuilder.job()
    String sqlPlusPrefix = "sqlplus"

    @Before
    void setupTables() {
        job.dataSource = job.embeddedDataSource()
        sqlPlus = new SqlPlus(job.dataSource)
         dropTables()
        createTable("foo", [bar: "int"])
        createTable("fooBar", [bar: "int"])
        createTable("baz", [bar: "int"])
        createTable("stuff", [bar: "int", foo: "varchar(20)", foobar: int])
        createTable("from_stuff", [bar: "int", foo: "varchar(20)"])

        sqlPlus.runBatch("foo", [[bar: 1], [bar: 2], [bar: 3], [bar: 4], [bar: 5], [bar: 6]])
        sqlPlus.runBatch("stuff", [[bar: 1, foo: "bar", foobar: 10], [bar:2, foo:"foobar", foobar: 20]])
    }

    @After
    void dropTables() {
        dropTable("foo")
        dropTable("fooBar")
        dropTable("baz")
        dropTable("stuff")
        dropTable("from_stuff")
    }

    void createTable(String tableName, Map columns) {

        String ddl = "create table ${tableName}("
        columns.each {key, value ->
            ddl += "${key} ${value},"
        }

        ddl = ddl.substring(0, ddl.size() - 1)
        ddl += ")"

        sqlPlus.execute(ddl)
    }

    void dropTable(String tableName) {
        String sqlDdl = "drop table ${tableName} if exists"
        sqlPlus.execute(sqlDdl)
    }

    @Test
    void testBasicRouting() {
        job.runRoute {
            from("${sqlPlusPrefix}:foo?dataSource=dataSource").to("${sqlPlusPrefix}:fooBar?dataSource=dataSource")
        }

        assert 6 == sqlPlus.firstRow("select count(*) as total from fooBar").total
    }

    @Test
    void testRoutingWithInsert() {
        job.runRoute {
            from("${sqlPlusPrefix}:foo?dataSource=dataSource").to("${sqlPlusPrefix}:insert into baz values(#bar)?dataSource=dataSource")
        }

        assert 6 == sqlPlus.firstRow("select count(*) as total from baz").total
    }

    @Test
    void testSendingGarbage() {
        job.runRoute {
            errorHandler(noErrorHandler())
            from("direct:iteratorBatchTest").to("${sqlPlusPrefix}:baz?dataSource=dataSource&batchSize=2")
        }

        try {
            //a string is not acceptable
            job.runRoute("direct:iteratorBatchTest", "not going to work")
            assert false: "exception should have occurred"
        } catch (CamelExecutionException ex) {
            assert ex.cause instanceof IllegalArgumentException
        }

    }

    @Test
    void testColumnsFunctionality() {

        job.runRoute {
            from("${sqlPlusPrefix}:stuff?dataSource=dataSource")
                .to("${sqlPlusPrefix}:from_stuff?columns=bar,foo&dataSource=dataSource")
        }

        assert 2 == sqlPlus.firstRow("select count(*) as total from from_stuff").total
    }

    @Test
    void testRoutingWithIterator() {
        job.runRoute {
            from("direct:iteratorBatchTest").to("${sqlPlusPrefix}:baz?dataSource=dataSource&batchSize=2")
        }

        def data = [[bar: 1], [bar: 2], [bar: 3]].iterator()
        job.runRoute("direct:iteratorBatchTest", data)

        assert 3 == sqlPlus.firstRow("select count(*) as total from baz").total
    }
}
