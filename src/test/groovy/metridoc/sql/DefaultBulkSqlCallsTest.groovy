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

import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 10/21/11
 * Time: 1:42 PM
 */
public class DefaultBulkSqlCallsTest {

    @Test
    void testBulkInsertFromMap() {
        def defaultSql = new DefaultBulkSqlCalls()
        def values = [foo: "tobar", bar: "tofoo"] as TreeMap
        assert "insert into baz(tofoo, tobar) select bar, foo from foobar" == defaultSql.getBulkInsert("foobar", "baz", values)
    }

    @Test
    void testBulkInsertWithColumnList() {
        def defaultSql = new DefaultBulkSqlCalls()
        def values = ["bar", "foo"]
        assert "insert into baz(bar, foo) select bar, foo from foobar" ==
            defaultSql.getBulkInsert("foobar", "baz", values)
    }

    @Test
    void testNoDupBulkInsertFromMap() {
        def defaultSql = new DefaultBulkSqlCalls()
        def values = [foo: "tobar", bar: "tofoo"] as TreeMap
        println defaultSql.getNoDuplicateBulkInsert("foobar", "baz", "tobar", values)
        assert "insert into baz(tofoo, tobar) select bar, foo from foobar on duplicate key update baz.tobar = baz.tobar" ==
                defaultSql.getNoDuplicateBulkInsert("foobar", "baz", "tobar", values)
    }

    @Test
    void testNoDupBulkInsertFromList() {
        def defaultSql = new DefaultBulkSqlCalls()
        def values = ["bar", "foo"]
        assert "insert into baz(bar, foo) select bar, foo from foobar on duplicate key update baz.foo = baz.foo" ==
                defaultSql.getNoDuplicateBulkInsert("foobar", "baz", "foo", values)
    }
}
