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
package metridoc.core

import org.apache.shiro.SecurityUtils
import org.springframework.dao.DataIntegrityViolationException

import static org.apache.commons.lang.StringUtils.EMPTY

class LdapRoleController {

    def roleMappingService

    static allowedMethods = [save: "POST", update: "POST", delete: ["DELETE"], list: "GET", index: "GET"]
    def static final reportName = "Manage LDAP role mapping"

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def index() {
        chain(action: "list")
    }

    def list() {
        def max = Math.min(params.max ? params.int('max') : 10, 100)
        params.max = max
        def groupCount = LdapRoleMapping.count()
        def showPagination = groupCount > max

        [
                ldapRoleMappingInstanceList: LdapRoleMapping.list(params),
                ldapRoleMappingInstanceTotal: groupCount,
                showPagination: showPagination,
                userGroups: roleMappingService.userGroupsAsList(SecurityUtils.getSubject().getPrincipal()) ?: []
        ]
    }

    def create() {

        [ldapRoleMappingInstance: new LdapRoleMapping(params)]
    }

    def save() {
        def groupname = params.name
        if (groupname == null || groupname == EMPTY) {
            flash.alert = "groupname has to be provided"
            render(view: "/ldapRole/list")
        } else if (!roleMappingService.isValidGroup(groupname)) {
            flash.alert = "Not a valid group name!"
            redirect(action: "list")
            return
        }
        def ldapRoleMappingInstance = new LdapRoleMapping(name: groupname)

        ldapRoleMappingInstance.with {

            roles = []
            def addRole = { roleName ->
                log.debug("adding role ${roleName} for user ${ldapRoleMappingInstance}")
                def role = ShiroRole.findByName(roleName) {}
                roles.add(role as ShiroRole)
            }
            def isAString = params.roles instanceof String
            if (isAString) {
                params.roles = [params.roles]
            }
            params.roles.each { roleName ->
                addRole(roleName)
            }

        }


        if (!ldapRoleMappingInstance.save(flush: true)) {
            flash.alert = "Could not save ${ldapRoleMappingInstance}"
            redirect(action: "list")
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'ldapRoleMapping.label', default: 'LDAP Group'), ldapRoleMappingInstance.name])
        redirect(action: "show", id: ldapRoleMappingInstance.id)
    }

    def show() {
        def ldapRoleMappingInstance = LdapRoleMapping.get(params.id)
        if (!ldapRoleMappingInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'ldapRoleMapping.label', default: 'LDAP Group'), params.id])
            redirect(action: "list")
            return
        }

        [ldapRoleMappingInstance: ldapRoleMappingInstance,
                userGroups: roleMappingService.userGroupsAsList(SecurityUtils.getSubject().getPrincipal())] ?: []
    }

    def edit() {
        def ldapRoleMappingInstance = LdapRoleMapping.get(params.id)
        if (!ldapRoleMappingInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'ldapRoleMapping.label', default: 'LDAP Group'), params.id])
            redirect(action: "list")
            return
        }

        [ldapRoleMappingInstance: ldapRoleMappingInstance]
    }

    def update() {

        def id = params.id
        def ldapRoleMappingInstance = LdapRoleMapping.get(params.id)
        if (!ldapRoleMappingInstance) {
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (ldapRoleMappingInstance.version > version) {
                flash.alert = "Another user has updated this LdapRoleMapping while you were editing"
                render(view: "/ldapRole/edit", model: [ldapRoleMappingInstance: ldapRoleMappingInstance])
                return
            }
        }

        ldapRoleMappingInstance.lock()
        ldapRoleMappingInstance.with {

            roles = []
            def addRole = { roleName ->
                log.debug("adding role ${roleName} for user ${ldapRoleMappingInstance}")
                def role = ShiroRole.findByName(roleName) {}
                roles.add(role as ShiroRole)
            }
            def isAString = params.roles instanceof String
            if (isAString) {
                params.roles = [params.roles]
            }
            params.roles.each { roleName ->
                addRole(roleName)
            }
        }

        if (!ldapRoleMappingInstance.save(flush: true)) {
            flash.message = "Save failed!"
            render(view: "/user/edit", model: [ldapRoleMappingInstance: ldapRoleMappingInstance])
            return
        }

        flash.info = "Group '${ldapRoleMappingInstance.name}' updated"
        redirect(action: "show", id: ldapRoleMappingInstance.id)
    }

    def delete() {

        def ldapRoleMappingInstance = LdapRoleMapping.get(params.id)
        if (!ldapRoleMappingInstance) {
            redirect(action: "list")
            return
        }

        try {
            ldapRoleMappingInstance.delete(flush: true)
            flash.info = "Group ${ldapRoleMappingInstance.name} deleted"
            redirect(action: "list")
            return
        }
        catch (DataIntegrityViolationException e) {
            log.error("error occurred trying to delete group ${ldapRoleMappingInstance}", e)
            redirect(action: "show", id: params.id)
            return
        }
    }


}
