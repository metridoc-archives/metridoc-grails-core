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
package metridoc.plugins.impl.iterators

import java.sql.ResultSet
import java.sql.ResultSetMetaData
import org.junit.Test
import static metridoc.plugins.iterators.IteratorTestSupport.exchange
import static metridoc.plugins.iterators.IteratorTestSupport.message

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 9/27/11
 * Time: 8:56 AM
 */
public class SqlIteratorTest {

    int index = 0

    @Test
    void testThatCreateCreatesADifferentGrid() {
        def grid = new SqlIterator()
        assert grid != grid.doCreate(resultSet())
    }

    @Test
    void endToEndTest() {
        def exchange = exchange(message(resultSet()))
        def grid = new SqlIterator().doCreate(resultSet())

        assert grid.hasNext()
        def row = grid.next()
        assert row.foo == "foo"
        assert row.bar == "bar"
        assert row.baz == 5

        assert grid.hasNext()
        row = grid.next()
        assert row.foo == "bar"
        assert row.bar == "foo"
        assert row.baz == 7

        assert grid.hasNext()
        row = grid.next()
        assert row.foo == "baz"
        assert row.bar == "bar"
        assert row.baz == 50

        assert !grid.hasNext()

        try {
            grid.next()
            assert false: "exception should have occurred"
        } catch (NoSuchElementException ex) {

        }
    }

    ResultSet resultSet() {
        [
            next: {
                if (index > 2) {
                    return false
                }
                index++
                return true
            },
            getMetaData: {
                resultSetMetaData()
            },
            getObject: {int i ->
                getObject(index, i)
            }

        ] as ResultSet
    }

    def getObject(int row, int column) {
        rows().get(row - 1).get(column - 1)
    }

    List<List> rows() {
        [
            ["foo", "bar", 5],
            ["bar", "foo", 7],
            ["baz", "bar", 50]
        ]
    }

    ResultSetMetaData resultSetMetaData() {
        [
            getColumnCount: {
                3
            },
            getColumnLabel: {int i ->
                switch (i) {
                    case 1:
                        return "foo"
                    case 2:
                        return "bar"
                    case 3:
                        return "baz"
                }
            }
        ] as ResultSetMetaData
    }


}
