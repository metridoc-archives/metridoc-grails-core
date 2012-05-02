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

import java.sql.BatchUpdateException
import java.sql.Statement
import javax.sql.DataSource
import metridoc.utils.PropertyUtils
import org.h2.jdbcx.JdbcConnectionPool
import org.h2.jdbcx.JdbcDataSource
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert
import java.sql.SQLException
import org.junit.Ignore

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/18/11
 * Time: 3:37 PM
 */
class SqlPlusTest extends Assert{

    SqlPlus sql

    void testRunningFile() {
        new SqlPlus(getDataSource()).runBatchFile(file: "batchFile")
    }

    @Before
    void createFoo() {
        sql = sql()
        dropTables()
        sql.execute("create table foo (bar int, baz varchar(5))")
        sql.execute("create table foobar (bam varchar(5), boo int)")
        sql.execute("create table blah (foo varchar(5), bar varchar(5))")
        sql.execute("create table baz (foo varchar(5), bar varchar(5))")
        sql.execute("create table bam (foobar varchar(5), barbar varchar(5))")
    }

    @After
    void dropFoo() {
        dropTables()
        sql.close()
    }

    void dropTables() {
        sql.execute("drop table foo if exists")
        sql.execute("drop table foobar if exists")
        sql.execute("drop table blah if exists")
        sql.execute("drop table baz if exists")
        sql.execute("drop table bam if exists")
    }

    @Test
    void testGetBulkSqlGetsTheSameProviderEachTimeAndIsNeverNull() {
        assert sql.bulkSqlCalls
        assert sql.bulkSqlCalls == sql.bulkSqlCalls
    }

    @Test
    void testBulkInsertWithColumns() {
        sql.execute("insert into baz values('foo', 'bar')")
        sql.execute("insert into baz values('foo', 'bar')")
        assert 2 == sql.bulkInsert("baz", "blah", ["foo", "bar"])
        assert 2 == sql.firstRow("select count(*) as total from blah").total
    }

    @Test
    void testBulkInsertWithColumnMap() {
        sql.execute("insert into baz values('foo', 'bar')")
        sql.execute("insert into baz values('foo', 'bar')")
        def columnMap = [foo: "foobar", bar: "barbar"]

        assert 2 == sql.bulkInsert("baz", "bam", columnMap)
        assert 2 == sql.firstRow("select count(*) as total from bam").total

    }

    //TODO: setup something for h2, these ignored tests only work on mysql
    @Ignore
    @Test
    void testBulkInsertWithColumnsNoDup() {
        sql.execute("insert into baz values('foo', 'bar')")
        sql.execute("insert into baz values('fool', 'bar')")
        sql.execute("insert into blah values('fool', 'bar')")

        assert 1 == sql.bulkInsertNoDup("baz", "blah", "foo", ["foo", "bar"])
        assert 2 == sql.firstRow("select count(*) as total from blah").total
    }

    @Ignore
    @Test
    void testBulkInsertWithColumnMapNoDup() {
        sql.execute("insert into baz values('foo', 'bar')")
        sql.execute("insert into baz values('fool', 'bar')")
        sql.execute("insert into bam values('fool', 'bar')")

        def columnMap = [foo: "foobar", bar: "barbar"]

        assert 1 == sql.bulkInsertNoDup("baz", "bam", "foobar", columnMap)
        assert 2 == sql.firstRow("select count(*) as total from bam").total
    }

    @Test
    void testIteratorInsert() {
        def data = [
            [bar: 5, baz: "foo"],
            [bar: 50, baz: "blam"],
            [bar: 10, baz: "boom"],
            [bar: 5, baz: "foo"],
            [bar: 5, baz: "foo"],
            [bar: 5, baz: "foo"],
        ]

    }

    @Test
    void testRetrievingPhases() {
        ConfigObject configObject = new PropertyUtils().getConfig("batchFile")
        assert configObject
        def phases = SqlPlus.getPhases(configObject)
        assert phases
        assert phases.keySet().iterator().next() == 1
        assert phases[1D].phaseName == "runBar"
    }

    @Test
    void testRunningBatchSqlFromFile() {
        sql.runBatchFile(fileName: "batchFile")
        assert 2 == total("baz")
        assert 1 == total("blah")
        assert "fooba" == firstRow("foo", "blah")
    }

    @Test
    void testExclusions() {
        sql.runBatchFile(fileName: "batchFile", exclude: ["willExclude"])
        assert 2 == total("baz")
        assert 1 == total("blah")
        assert "hey" == firstRow("foo", "blah")
    }

    @Test
    void testExclusionCondition() {
        def exclusion = ["blah.boom", "boom"]
        assert !SqlPlus.exclude(exclusion, "foo")
        assert SqlPlus.exclude(exclusion, "blah.boom")
    }

    @Test
    void testExcludingSpecificSql() {
        sql.runBatchFile(fileName: "batchFile", exclude: ["runFoo.runIt"])
        assert 1 == total("baz")
        assert 1 == total("blah")
        assert "fooba" == firstRow("foo", "blah")
    }

    @Test
    void testCreatingSql() {
        def insert = SqlPlus.getInsertStatement("foo", [bar: 5])
        assert "insert into foo (bar) values (?)" == insert

        insert = SqlPlus.getInsertStatement("foo", [baz: 5, bar: 7])
        assert "insert into foo (bar, baz) values (?, ?)" == insert

        insert = SqlPlus.getInsertStatement("foo", [bar: 5, baz: "blah"])
        assert "insert into foo (bar, baz) values (?, ?)" == insert

        insert = SqlPlus.getInsertStatement("foo", [bar: 5, baz: "bl'ah"])
        assert "insert into foo (bar, baz) values (?, ?)" == insert
    }

    @Test
    void testInsertingBatch() {
        sql.runBatch("foo", [[bar: 5, baz: "blah"], [bar: 50, baz: "blaz"]])
        assert 2 == total("foo")
        sql.runBatch("foo", [bar: 5, baz: "blah"])
        assert 3 == total("foo")
    }

    @Test
    void testParamOrderFromInsert() {
        def params = SqlPlus.orderedParamsFromInsert(':boo foobar #bam blight :bar $stuff')
        assert "boo" == params[0]
        assert "bam" == params[1]
        assert "bar" == params[2]
        assert "stuff" == params[3]
    }

    @Test
    void getInsertWithNamedParameters() {
        assert "insert into foo (bar) values (?)" ==
                SqlPlus.getInsertStatementFromParamInsert("insert into foo (bar) values (:bar)")
        assert "insert into foo (bar) values (?)" ==
                SqlPlus.getInsertStatementFromParamInsert('insert into foo (bar) values ($bar)')
        assert "insert into foo (bar) values (?)" ==
                SqlPlus.getInsertStatementFromParamInsert("insert into foo (bar) values (#bar)")
    }

    @Test
    void testBatchInsertWithStatement() {

        int[] result = sql.runBatch("insert into foobar(bam, boo) values (:baz, :bar)",
                [
                        [bar: 1, baz: "a", blowy: 89], //blowy should be ignored
                        [bar: 2, baz: "b"],
                        [bar: 3, baz: "c"],
                        [bar: 4, baz: "d"],
                        [bar: 5, baz: "e"],
                        [bar: 6, baz: "f"]
                ]
        )

        sql.eachRow("select * from foobar") {row ->
            if (row.boo == 1) {
                assert "a" == row.bam
            }

            if (row.boo == 4) {
                assert "d" == row.bam
            }
        }

        assert result.size() == 6
        result.each {
            assert it == 1
        }
    }

    @Test
    void testFailedUpdate() {
        assert !SqlPlus.failed(1)
        assert SqlPlus.failed(Statement.EXECUTE_FAILED)
    }

    @Test
    void testFailure() {

        try {
            int[] result = sql.runBatch("insert into foo(bar, baz) values (:bar, :baz)",
                    [
                            [bar: 1, baz: "a"],
                            [bar: 2, baz: "babasas"],
                            [bar: 3, baz: "c"],
                    ]
            )
            fail("exception should have occurred")
        } catch (BatchUpdateException ex) {
            def updates = ex.updateCounts
            assert updates.size() == 3
            assert 1 == updates[0]
            assert Statement.EXECUTE_FAILED == updates[1]
            assert 1 == updates[2]
        }

        //make sure everything was rolled back
        assert 0 == total("foo")
    }

    int total(String tableName) {
        return firstRow("count(*) as total", tableName, "total")
    }

    def firstRow(String what, String from, String grab = what) {
        String firstRow = "select ${what} from ${from}"
        sql.firstRow(firstRow)."${grab}"
    }

    SqlPlus sql() {
        new SqlPlus(getDataSource())
    }

    DataSource getDataSource() {
        return new JdbcConnectionPool(new JdbcDataSource(URL: "jdbc:h2:mem:test"))
    }

}
