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


}
