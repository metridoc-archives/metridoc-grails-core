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

import metridoc.reports.ShiroRole
import metridoc.reports.ShiroUser
import org.apache.shiro.SecurityUtils
import org.apache.shiro.crypto.hash.Sha256Hash
import org.springframework.dao.DataIntegrityViolationException

class UserController {

    static homePage = [
            title: "Manage Users",
            adminOnly: true,
            description: """
                Create, update and delete users and roles.  This does not affect external authc and authz via ldap
                or other means.  All operations are used against MetriDoc's internal database.
            """
    ]

    static allowedMethods = [save: "POST", update: "POST", delete: ['DELETE', "POST"], list: "GET", index: "GET"]
    static accessControl = {
        role(name: "ROLE_ADMIN")
    }
    def userService

    def index() {
        chain(action: "list")
    }

    def list() {
        def max = Math.min(params.max ? params.int('max') : 10, 100)
        params.max = max
        def userCount = ShiroUser.count()
        def showPagination = userCount > max
        [
                currentUserName: SecurityUtils.getSubject().getPrincipal(),
                shiroUserInstanceList: ShiroUser.list(params),
                shiroUserInstanceTotal: userCount,
                showPagination: showPagination
        ]
    }

    def create() {
        [shiroUserInstance: new ShiroUser(params)]
    }

    def save() {

        def password = params.get('password')
        def confirm = params.get('confirm')

        def shiroUserInstance = new ShiroUser(username: params.get('username'),password: password, confirm: confirm, passwordHash: new Sha256Hash(password).toHex(), emailAddress: params.get('emailAddress'))
        userService.addRolesToUser(shiroUserInstance, params.roles)

        if (!shiroUserInstance.save(flush: true)) {
            render(view: "/user/create", model: [shiroUserInstance: shiroUserInstance])
            return
        }
        if (password.toString().length() < 5 || password.toString().length() > 15) {
            shiroUserInstance.errors.rejectValue('password', 'blank.password', 'Password length must be within 5-15')
            render(view: "/user/create", model: [shiroUserInstance: shiroUserInstance])
            shiroUserInstance.delete()
            return
        }
        if (password != confirm) {
            shiroUserInstance.errors.rejectValue('password', '',"Passwords don't match")
            render(view: "/user/create", model: [shiroUserInstance: shiroUserInstance])
            shiroUserInstance.delete()
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), shiroUserInstance.username])
        redirect(action: "show", id: shiroUserInstance.id)
    }

    def show() {
        def shiroUserInstance = ShiroUser.get(params.id)
        if (!shiroUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "list")
            return
        }

        [
                currentUserName: SecurityUtils.getSubject().getPrincipal(),
                shiroUserInstance: shiroUserInstance
        ]
    }

    def edit() {
        def shiroUserInstance = ShiroUser.get(params.id)
        if (!shiroUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "list")
            return
        }

        [
                currentUserName: SecurityUtils.getSubject().getPrincipal(),
                shiroUserInstance: shiroUserInstance
        ]
    }

    def update() {

        def shiroUserInstance = ShiroUser.get(params.id)
        if (!shiroUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "list")
            return
        }

        if (params.version) {
            def version = params.version.toLong()
            if (shiroUserInstance.version > version) {
                shiroUserInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'shiroUser.label', default: 'ShiroUser')] as Object[],
                        "Another user has updated this ShiroUser while you were editing")
                render(view: "/user/edit", model: [shiroUserInstance: shiroUserInstance])
                return
            }
        }

        if (params.get('password') != params.get('confirm')) {
            shiroUserInstance.errors.rejectValue('password', '',"Passwords don't match")
            render(view: "/user/edit", model: [shiroUserInstance: shiroUserInstance])
            return
        }

        shiroUserInstance.with {
            username = params.username
            emailAddress = params.emailAddress
            if (params.password) {
                password = params.password
                confirm = params.confirm
                shiroUserInstance.setPasswordHash(new Sha256Hash(password).toHex())
            }
            roles = []
            def addRole = {roleName ->
                log.debug("adding role ${roleName} for user ${shiroUserInstance}")
                def role = ShiroRole.find {
                    name == roleName
                }
                roles.add(role)
            }
            def isAString = params.roles instanceof String
            if (isAString) {
                params.roles = [params.roles]
            }
            params.roles.each {roleName ->
                addRole(roleName)
            }

        }

        if (!shiroUserInstance.save(flush: true)) {
            if (shiroUserInstance.errors) {
                log.error("There were errors trying to update ${shiroUserInstance}")
                shiroUserInstance.errors.each {error ->
                    log.error("Error ${error} occurred trying to update user ${shiroUserInstance}")
                }
            }
            render(view: "/user/edit", model: [shiroUserInstance: shiroUserInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), shiroUserInstance.username])
        redirect(action: "show", id: shiroUserInstance.id)
    }

    def delete() {

        def shiroUserInstance = ShiroUser.get(params.id)
        if (!shiroUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "list")
            return
        }
        String username = shiroUserInstance.username
        try {
            shiroUserInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), username])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), username])
            redirect(action: "show", id: params.id)
        }
    }
}
