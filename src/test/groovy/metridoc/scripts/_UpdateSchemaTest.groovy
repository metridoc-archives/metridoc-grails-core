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
package metridoc.scripts

import groovy.sql.Sql
import metridoc.dsl.JobBuilder
import org.junit.Before
import org.junit.Test
import java.sql.SQLException
import org.junit.After

/**
 * Created with IntelliJ IDEA.
 * User: tbarker
 * Date: 4/28/12
 * Time: 9:54 AM
 * To change this template use File | Settings | File Templates.
 */
class _UpdateSchemaTest extends Script {


    @Before
    void setup() {
        run()
    }

    @After
    void cleanUp() {
        runLiquibase = true
    }

    @Override
    Object run() {
        JobBuilder.isJob(this)
        liquibaseFile = "testFiles/metridoc/scripts/foo.xml"
        dataSource = embeddedDataSource()
        sql = new Sql(dataSource)
        sql.execute("drop table foo if exists")
        includeTargets << _UpdateSchema

    }

    @Test
    void "test liquibase schema update"() {
        executeTargets(["updateSchema"])
        assert 0 == sql.firstRow("select count(*) as total from foo").total
    }

    @Test
    void "schema does not run if runLiquibase is false" () {
        runLiquibase = false
        executeTargets(["updateSchema"])
        try {
            sql.firstRow("select count(*) as total from foo").total
            assert false : "exception should have occurred"
        } catch (SQLException exception) {
        }
    }
}


