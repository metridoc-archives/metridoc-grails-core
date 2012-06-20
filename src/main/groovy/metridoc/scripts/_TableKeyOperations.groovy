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

import metridoc.tools.DatabaseTool

includeTool << DatabaseTool
includeTargets << _BaseUtilities

doKeyOperations = true
keyOperationTables = []

doEnableKeys = [
    mysql: {
        sql.execute("alter table ${table} enable keys")
    }
]

doDisableKeys = [
    mysql: {
        sql.execute("alter table ${table} disable keys")
    }
]

def handleKeyOperation(String keyOperation) {
    if (doKeyOperations) {
        def dbType = databaseTool.getDatabaseType()

        if (keyOperationTables && dbType) {
            def operation = binding.variables.get(keyOperation)
            if (operation) {
                Closure operationByDatabaseType = operation.get(dbType)
                keyOperationTables.each {
                    def delegate = [table: it, sql: databaseTool.getSql()]
                    operationByDatabaseType.delegate = delegate
                    operationByDatabaseType.resolveStrategy = Closure.DELEGATE_FIRST
                    operationByDatabaseType.call()
                }
            }
        }
    }
}

target(disableTableKeys: "disable keys on a table based on variable disableKeyTables") {
    handleKeyOperation("doDisableKeys")
}

target(enableTableKeys: "enables keys on a table based on enableKeyTables") {
    handleKeyOperation("doEnableKeys")
}