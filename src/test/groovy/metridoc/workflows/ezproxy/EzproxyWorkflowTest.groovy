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
package metridoc.workflows.ezproxy

import groovy.sql.Sql
import metridoc.utils.DataSourceUtils
import metridoc.workflows.EzproxyWorkflow
import org.junit.Test

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/13/12
 * Time: 10:52 AM
 */
class EzproxyWorkflowTest {

    def dataSource = DataSourceUtils.embeddedDataSource()
    def sql = new Sql(dataSource)

    @Test
    void testGetLoadFileIntoTableDefaults() {
        def dataSource = DataSourceUtils.embeddedDataSource()
        def workflow = new EzproxyWorkflow(
            dataSource: dataSource,
            schemaUpdateConfig: [
                runLiquibase: false
            ]
        )
        def schemaUpdate = workflow.getSchemaUpdate()

        assert !schemaUpdate.runLiquibase
        assert dataSource == schemaUpdate.liquibaseDataSource
    }

    @Test
    void testUpdatingTheSchema() {
        def dataSource = DataSourceUtils.embeddedDataSource()

        new EzproxyWorkflow(
            dataSource: dataSource,
            pipeline: ["updateSchema"]
        ).run()

        def sql = new Sql(dataSource)
        sql.firstRow("select count(*) from ezproxy_loading")
    }

    @Test
    void testEnablingKeys() {
        def workflow = new EzproxyWorkflow(
            dataSource: dataSource,
            pipeline: ["enableTableKeys"]
        )
        workflow.loadGantScriptsAndTargets()

        def enableKeysRan = false
        workflow.keyOperationTables.add "foo"
        workflow.doEnableKeys.put("h2") {
            enableKeysRan = true
        }

        workflow.run()

        assert enableKeysRan
    }
}
