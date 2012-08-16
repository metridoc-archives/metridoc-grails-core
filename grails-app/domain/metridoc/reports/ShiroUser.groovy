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

/**
 * Created by IntelliJ IDEA.
 * User: tbarker
 * Date: 5/22/12
 * Time: 11:15 AM
 */
class ShiroUser {
    String username
    String passwordHash
    String password
    String confirm
    String emailAddress
    static transients = ['password', 'confirm']

    static mapping = {
        datasource 'admin'
    }
    static hasMany = [roles: ShiroRole, permissions: String]

    static constraints = {
        username(nullable: false, blank: false, unique: true)
        emailAddress(email: true, nullable: true)
        password(blank: false, size: 5..15, matches: /[\S]+/, validator: { val, obj ->
            return obj.password.equals(obj.confirm)
        })

    }
}
