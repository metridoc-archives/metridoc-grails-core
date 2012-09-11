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
package metridoc.reports

import groovy.sql.Sql
import org.junit.Test

import java.sql.SQLException

class DatabaseMigrationTests {

    def dataSource_admin

    @Test
    void "test that admin tables exist"() {
        def sql = new Sql(dataSource_admin)
        //table exists
        assert tableExists(sql, "shiro_user")
        assert tableExists(sql, "application_properties")
    }

    @Test
    void "test ranged by id queries"() {
        assert ShiroUser.findAllByIdBetween(0L, Long.MAX_VALUE).size() > 0
    }

    boolean tableExists(sql, tableName) {
        try {
            sql.firstRow("select count(*) as total from ${tableName}" as String).total
            return true
        } catch (Exception e) {
            return false
        }
    }
}
