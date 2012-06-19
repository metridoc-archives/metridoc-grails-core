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
package metridoc.workflows

import groovy.util.logging.Slf4j
import javax.sql.DataSource
import metridoc.plugins.schema.MetridocLiquibase

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 2/8/12
 * Time: 8:55 AM
 */

@Slf4j
class SchemaUpdate extends Script{

    boolean runLiquibase = true
    String projectName
    DataSource dataSource
    List<String> schemaFiles

    def run() {
        if (runLiquibase) {
            assert dataSource: "dataSource must exist for schema to update"
            assert getSchemaFiles(): "schemaFiles must exist for schema to update"
            schemaFiles.each {
                new MetridocLiquibase(dataSource: dataSource, schema: it).runMigration()
            }
        }
    }

    void setSchema(String schema) {
        schemaFiles = [schema]
    }

    List<String> getSchemaFiles() {
        if (schemaFiles) {
            return schemaFiles
        }

        if (getProjectName()) {
            schemaFiles = ["schemas/${getProjectName()}/${getProjectName()}Schema.xml" as String]
        }

        return schemaFiles
    }
}



