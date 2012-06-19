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

enableKeysSql = [
    mysql: {table ->
        "alter table ${table} enable keys"
    }
]

disableKeysSql = [
    mysql: {table ->
        "alter table ${table} disable keys"
    }
]

target(disableKeys: "disable keys on a table based on variable disableKeyTables") {
    if (hasVariable("disableKeyTables")) {
        assert disableKeyTables instanceof List: "disableKeyTables with values ${disableKeyTables} must be of type List"
        def dbType = databaseTool.getDatabaseType()
        if (dbType) {
            def disableSql = disableKeysSql[dbType]

            if (disableSql) {
                def sql = databaseTool.getSql()
                disableKeyTables.each {
                    message("disableKeys", "disabling keys for table ${it}")
                    sql.execute(disableSql)
                }
            } else {
                message("disableKeys", "there is no sql for dbType ${dbType} to disable keys")
            }
        }
    }
}

target(enableKeys: "enables keys on a table based on enableKeyTables") {
    if (hasVariable("enableKeyTables")) {
        assert enableKeyTables instanceof List: "enableKeyTables with values ${enableKeyTables} must be of type List"
        def dbType = databaseTool.getDatabaseType()
        if (dbType) {
            def enableSql = enableKeysSql[dbType]

            if (enableSql) {
                def sql = databaseTool.getSql()
                enableKeyTables.each {
                    message("enableKeys", "enabling keys for table ${it}")
                    sql.execute(enableSql)
                }
            } else {
                message("enableKeys", "there is no sql for dbType ${dbType} to enable keys")
            }
        }
    }
}