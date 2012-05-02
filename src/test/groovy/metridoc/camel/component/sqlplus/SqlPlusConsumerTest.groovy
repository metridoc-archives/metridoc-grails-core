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

import org.junit.Test
import org.apache.camel.Processor
import metridoc.dsl.JobBuilder;

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 8/4/11
 * Time: 4:03 PM
 */
public class SqlPlusConsumerTest {

    @Test
    void testGetQueryWhenItIsTable() {
        def endpoint = new SqlPlusEndpoint(query: "foo")
        def consumer = endpoint.createConsumer([:] as Processor)
        assert "select * from foo" == consumer.tableQuery
    }

    /**
     * just a quick test to make sure the mixin works properly
     */
    @Test
    void testRetrieveSqlFromMixin() {
        def dataSource = JobBuilder.job().embeddedDataSource()
        def endpoint = new SqlPlusEndpoint(query: "foo", dataSource: dataSource)
        def consumer = endpoint.createConsumer([:] as Processor)
        def sql = consumer.sql
        assert sql != null
        sql.execute("drop table foo if exists")
        sql.execute("create table foo (bar int)")
        assert 0 == sql.firstRow("select count(*) as total from foo").total
    }

}
