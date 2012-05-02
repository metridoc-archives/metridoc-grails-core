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

import groovy.sql.Sql

/**
 * Institution-specific script used to update user related information
 *
 * User: pkeavney
 * Date: 3/15/12
 */

    static update() { users.metaClass.invokeConstructor() }

    Sql sql = Sql.newInstance( repository )

    //    sql.execute( "replace into patron_org_rank select convert(md5(patron_id),char(32)) as user_id, org, rank from metridoc.patron_rank where convert(md5(patron_id),char(32)) in (select user_id from ill_user_info)" )
    //    sql.execute( "update ill_user_info u join patron_org_rank p using (user_id) set u.org = p.org, u.rank=p.rank" )
