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
package metridoc.plugins.datasource

import groovy.sql.Sql
import metridoc.dsl.JobBuilder
import metridoc.plugins.datasource.providers.CommonsDataSourceProvider
import metridoc.plugins.datasource.providers.H2DataSourceProvider
import metridoc.utils.DataSourceUtils
import org.junit.BeforeClass
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/10/11
 * Time: 11:04 AM
 */
public class DataSourcePluginTest {

    @BeforeClass
    static void setUpProviders() {
        DataSourceUtils.dataSourceProviders["h2"] = H2DataSourceProvider
    }

    @Test
    def void testGetDataSourceProvider() {
        assert H2DataSourceProvider == DataSourceUtils.getDataSourceProvider("jdbc:h2:mem")
    }

    @Test(expected = IllegalArgumentException)
    def void testExceptionWhenThereIsAPoorlyFormedUrl() {
        DataSourceUtils.getDataSourceProvider("foo:bar")
    }

    @Test
    def void testGettingCommonsProviderWhenThereIsNoProvider() {
        assert CommonsDataSourceProvider == DataSourceUtils.getDataSourceProvider("jdbc:bar:baz")
    }

    @Test
    void testEmbeddedDataSource() {
        new EmbeddedDataSourceScript().run()
    }
}

class EmbeddedDataSourceScript extends Script {

    @Override
    Object run() {
        JobBuilder.isJob(this)
        def embedded = embeddedDataSource()
        def sql = new Sql(embedded)

        sql.execute("drop table foo if exists")
        sql.execute("create table foo(bar int)")
        sql.execute("insert into foo values(5)")
        sql.execute("insert into foo values(4)")
        sql.execute("insert into foo values(3)")
        sql.execute("insert into foo values(2)")
        sql.execute("insert into foo values(1)")
        assert 5 == sql.firstRow("select count(*) as total from foo").total
    }
}