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
package metridoc.camel.component.poll

import groovy.sql.Sql
import javax.sql.DataSource
import metridoc.dsl.JobBuilder
import metridoc.test.BaseTest
import org.junit.After
import org.junit.Before
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/10/11
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
class PollRouteTest extends BaseTest {

    def DataSource database
    def Sql sql

    @Before
    def void setupDatabase() {
        database = JobBuilder.job().embeddedDataSource()
        sql = new Sql(database)
        sql.execute("drop table if exists foo")
        sql.execute("create table foo (name varchar(50))")

        (1..4).each {
            String sqlStatement = "insert into foo (name) values ('bar_${it}')"
            sql.execute(sqlStatement)
        }
    }

    @After
    def void tearDownDatabase() {
        sql.execute("drop table if exists foo")
        database.dispose()
    }

    @Test
    def void testSqlPlusComponentWithNewPollingStrategy() {
        JobBuilder.run {
            defaultJob {
                dataSource = database

                def Set barSet = []
                (1..4).each {
                    barSet.add("bar_${it}".toString())
                }

                assert !barSet.empty
                runRoute ({
                    from("sqlplus:select * from foo?dataSource=dataSource").process {

                        def rowSet = it.in.body
                        while(rowSet.next()) {
                            barSet.remove rowSet.getString("name")
                        }
                    }
                })
                assert barSet.empty
            }
        }
    }
}
