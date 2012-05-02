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
import org.apache.commons.dbcp.BasicDataSource
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 7/11/11
 * Time: 10:29 AM
 */
class DataSourceJobTest {

    @Test
    def void testCallingOtherJob() {
        def job = JobBuilder.job()
        job.runJob DataSourceJob
        job.runJob HsqlDbJob
    }
}

/**
 * hsql is not officially supported by the api.  Just checking that it defaults to basic datasource by default.
 */
class HsqlDbJob extends Script {
    @Override
    Object run() {
        def dataSource = dataSource(user: "sa", password: "", url: "jdbc:hsqldb:mem:test", driverClass: "org.hsqldb.jdbcDriver")
        assert dataSource instanceof BasicDataSource
        def sql = new Sql(dataSource)
        sql.execute("create table foo(bar int)")
        assert 0 == sql.firstRow("select count(*) as total from foo").total
    }
}
