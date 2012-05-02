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
package metridoc.camel.impl.iterator

import org.junit.Test
import org.apache.camel.Exchange
import org.apache.camel.Message
import java.sql.ResultSet
import metridoc.camel.iterator.IteratorCreator
import metridoc.dsl.JobBuilder
import metridoc.plugins.datasource.DataSourcePlugin
import groovy.sql.Sql
import groovy.sql.GroovyRowResult
import org.junit.Assert
import metridoc.utils.ColumnConstrainedMap;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/4/11
 * Time: 10:58 AM
 */
public class ResultSetIteratorCreatorTest extends Assert{

    @Test
    void canCreateIteratorWithExchangeContainingResultSet() {

        def resultSet = [:] as ResultSet

        def exchange = getExchange(resultSet)
        def iterator = iteratorCreator().create(exchange)
        assert resultSet == iterator.resultSet
    }

    @Test(expected = IllegalArgumentException.class)
    void cannotCreateIteratorIfExchangeDoesNotContainResultSet() {
        def exchange = getExchange(null)
        iteratorCreator().create(exchange)
    }

    @Test
    void canCreateIteratorIfBodyIsAlreadyResultSet() {
        def resultSet = [:] as ResultSet
        def iterator = iteratorCreator().create(resultSet)
        assert resultSet == iterator.resultSet
    }

    @Test(expected = IllegalArgumentException.class)
    void exceptionThrownIfBodyNotExchangeOrResultSet() {
        iteratorCreator().create("foo")
    }

    @Test(expected = IllegalArgumentException.class)
    void exceptionThrownIfBodyIsNull() {
        iteratorCreator().create(null)
    }

    @Test
    void hasNextIsFalseIfNoNewRowsExist() {
        def resultSet = [
                next: {false}
        ] as ResultSet

        def iterator = iteratorCreator()
        iterator.resultSet = resultSet
        assert !iterator.hasNext()
    }

    @Test(expected = NoSuchElementException.class)
    void exceptionThrownIfNextCalledAndNoElementsExist() {
        def resultSet = [
                next: {false}
        ] as ResultSet

        def iterator = iteratorCreator()
        iterator.resultSet = resultSet
        assert !iterator.hasNext()
        iterator.next()
    }

    @Test
    void endToEndTestWithEmbeddedDatabase() {
        def plugin = JobBuilder.job() as DataSourcePlugin
        def dataSource = plugin.embeddedDataSource()
        def sql = new Sql(dataSource)
        sql.execute("drop table foo if exists")
        sql.execute("create table foo (bar int)")
        sql.execute("insert into foo values (5)")
        sql.execute("insert into foo values (12)")
        sql.query("select * from foo order by bar") {ResultSet resultSet ->
            def iterator = iteratorCreator().create(resultSet)
            assert iterator.hasNext()
            assert 5 == iterator.next().bar
            assert iterator.hasNext()
            assert 12 == iterator.next().bar
            assert !iterator.hasNext()
        }
    }

    @Test
    void closesResultSetOnClose() {
        boolean closeCalled = false
        def resultSet = [close: {closeCalled = true}] as ResultSet
        def iterator = iteratorCreator()
        iterator.resultSet = resultSet
        iterator.close()
        assert closeCalled
        assert iterator.closed
    }

    @Test
    void exceptionsIgnoredOnResultSetWhenCloseCalled() {
        boolean closeCalled = false
        def resultSet = [close: {closeCalled = true; throw new RuntimeException("oops")}] as ResultSet
        def iterator = iteratorCreator()
        iterator.resultSet = resultSet
        iterator.close()
        assert closeCalled
    }

    @Test(expected = UnsupportedOperationException.class)
    void removeIsNotSupported() {
        iteratorCreator().remove()
    }

    @Test
    void testThatColumnConstrainedMapConversionsWork() {
        def original = new GroovyRowResult([foo:"bar"])
        def columnConstrained = new ColumnConstrainedMap(original, ["FOO"] as Set)
        def finalResult = new GroovyRowResult(columnConstrained)

        assert 1 == finalResult.size()
    }

    @Test
    void ifClosedHasNextReturnsFalse() {
        def iterator = iteratorCreator()
        def resultSet = [
            toRowResult: {
                new GroovyRowResult([foo: "bar"])
            },
            next: {
                true
            },
            hasNext: {
                true
            }
        ] as ResultSet
        iterator.resultSet = resultSet
        iterator.closed = true
        assert !iterator.hasNext()
        try {
            iterator.next()
            fail("exception should have occurred")
        } catch (NoSuchElementException ex) {
        }

    }

    IteratorCreator iteratorCreator() {
        return new ResultSetIteratorCreator()
    }

    Exchange getExchange(Object body) {
        def message = [getBody: {Class clazz ->
            assert clazz == ResultSet
            return body
        }] as Message

        [getIn: {message}, toString: {"test Exchange"}] as Exchange
    }
}
