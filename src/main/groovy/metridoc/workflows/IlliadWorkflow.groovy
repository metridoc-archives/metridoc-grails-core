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
import metridoc.workflows.sqlStatements
import metridoc.workflows.dataStores
import metridoc.workflows.dataSources
import metridoc.workflows.routeRunner
import metridoc.workflows.lending
import metridoc.workflows.borrowing
import metridoc.workflows.schema
import metridoc.workflows.users
import metridoc.workflows.demographics

/**
 * script used to control Illiad to Metridoc ingestion and update process
 *
 * User: pkeavney
 * Date: 3/15/12
 */

@Slf4j
class IlliadWorkflow extends Workflow {

    static execute() {
        new IlliadWorkflow().run()
    }

    @Override
    protected void configureWorkflow() {
        pipeline = [dataSources.connect(),
                    schema.update(),
                    sqlStatements.create(),
                    dataStores.clear(),
                    routeRunner.execute(),
                    lending.update(),
                    borrowing.update(),
                    users.update(),
                    demographics.update()
        ]
    }
}
