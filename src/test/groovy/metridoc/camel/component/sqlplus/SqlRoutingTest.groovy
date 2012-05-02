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

import groovy.sql.Sql
import javax.sql.DataSource
import metridoc.utils.DataSourceUtils
import org.apache.camel.CamelContext
import org.apache.camel.EndpointInject
import org.apache.camel.builder.RouteBuilder
import org.apache.camel.component.mock.MockEndpoint
import org.apache.camel.impl.DefaultCamelContext
import org.apache.camel.impl.SimpleRegistry
import org.apache.camel.test.junit4.CamelTestSupport
import org.junit.Before
import org.junit.Test
import org.junit.After

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 3/9/12
 * Time: 10:05 AM
 */
class SqlRoutingTest extends CamelTestSupport {

    DataSource dataSource = DataSourceUtils.embeddedDataSource()
    Sql sql = new Sql(dataSource)
    @EndpointInject(uri = "mock:sqlLikTest")
    MockEndpoint endpoint

    @Test
    void testWithLikePercents() {
        endpoint.expectedCount = 1
        endpoint.reset()
        endpoint.assertIsSatisfied()
    }

    @Override
    protected CamelContext createCamelContext() {
        def registry = new SimpleRegistry()
        registry.put("dataSource", dataSource)
        return new DefaultCamelContext(registry)
    }

    @After
    void deleteFoo() {
        sql.execute("drop table foo if exists")
    }

    @Override
    protected RouteBuilder createRouteBuilder() {
        sql.execute("drop table foo if exists")
        sql.execute("create table FOO(bar varchar(10))")
        sql.execute("insert into FOO values ('tommy')")
        sql.execute("insert into FOO values ('pete')")

        new RouteBuilder() {

            @Override
            void configure() {

                String fromUri = "sqlplus:select * from foo where bar like '%2525omm%2525'?dataSource=dataSource"

                from(fromUri).to("mock:sqlLikeTest")
            }
        }
    }


}
