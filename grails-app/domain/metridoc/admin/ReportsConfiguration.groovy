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
package metridoc.admin

class ReportsConfiguration {

    String name
    String displayName
    /**
     * only allow 1 role per report configuration... just making this easier so I can get it done,
     * maybe change this later?
     */
    String role

    static constraints = {
        name(nullable: false, unique: true, blank: false)
        displayName(nullable: false)
        role(nullable: false, blank: false, matches: /ROLE_.+/)
    }

    static mapping = {
        datasource 'admin'
    }
}
