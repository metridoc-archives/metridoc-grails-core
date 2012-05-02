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

/**
 * script used to execute Camel routing from Illiad source to Metridoc repository
 *
 * User: pkeavney
 * Date: 3/15/12
 */
import static metridoc.dsl.JobBuilder.isJob

isJob(this)

    static execute() { routeRunner.metaClass.invokeConstructor() }

    runRoute {
        from("sqlplus:${groupSqlStmt}?dataSource=source").to("sqlplus:ill_group?dataSource=repository")
        from("sqlplus:${groupLinkSqlStmt}?dataSource=source").to("sqlplus:ill_lender_group?dataSource=repository")
        from("sqlplus:${lenderAddrSqlStmt}?dataSource=source").to("sqlplus:ill_lender_info?dataSource=repository")
        from("sqlplus:${referenceNumberSqlStmt}?dataSource=source").to("sqlplus:ill_reference_number?dataSource=repository")
        from("sqlplus:${transactionSqlStmt}?dataSource=source").to("sqlplus:ill_transaction?dataSource=repository")
        from("sqlplus:${lendingSqlStmt}?dataSource=source").to("sqlplus:ill_lending?dataSource=repository")
        from("sqlplus:${borrowingSqlStmt}?dataSource=source").to("sqlplus:ill_borrowing?dataSource=repository")
        from("sqlplus:${userSqlStmt}?dataSource=source").to("sqlplus:ill_user_info?dataSource=repository")
    }
