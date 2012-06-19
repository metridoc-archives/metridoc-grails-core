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

import metridoc.dsl.JobBuilder

/*
 * Runs liquibase and updates a schema.  Properties required are:
 *
 *     liquibaseFile - name of liquibase file on the classpath
 *     dataSource or liquibaseDataSource - data source that should be used to update the schema
 */

JobBuilder.isJob(this)

target(updateSchema:"updates the schema for the target dataSource") {

    def runLiquibase = binding.variables.containsKey("runLiquibase") ? binding.variables.runLiquibase : true

    if (runLiquibase) {
        if(binding.variables.liquibaseDataSource) {
            message("schema", "updating schema based on liquibaseDataSource variable")
            liquibase(schema:liquibaseFile, dataSource:liquibaseDataSource)
        } else {
            message("schema", "updating schema based on dataSource variable")
            liquibase(schema:liquibaseFile, dataSource:dataSource)
        }
    } else {
        message("schema", "no schemas were updated since runLiquibase is false")
    }
}