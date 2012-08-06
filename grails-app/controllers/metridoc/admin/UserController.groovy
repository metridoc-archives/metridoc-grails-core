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

import metridoc.reports.ShiroUser
import org.apache.shiro.crypto.hash.Sha256Hash
import org.springframework.dao.DataIntegrityViolationException
import org.apache.shiro.SecurityUtils

class UserController {
    static allowedMethods = [save: "POST", update: "POST", delete: "DELETE", list: "GET", index: "GET"]
    def static final reportName = "Manage Users"
    static final adminOnly = true
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
        def pwConfirm = params.get('confirm')
        if (password != pwConfirm) {
            flash.message = message(code: 'user.password.dontmatch', default: 'Passwords not match')
            render(view: "/user/create")
            return
        }

        def shiroUserInstance = new ShiroUser(username: params.get('username'), passwordHash: new Sha256Hash(password).toHex(), emailAddress: params.get('emailAddress'))
        userService.addRolesToUser(shiroUserInstance, params.roles)

        if (!shiroUserInstance.save(flush: true)) {
            render(view: "/user/create", model: [shiroUserInstance: shiroUserInstance])
            return
        }

        flash.message = message(code: 'default.created.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), shiroUserInstance.id])
        redirect(action: "show", id: shiroUserInstance.id)
    }

    def show() {
        def shiroUserInstance = ShiroUser.get(params.id)
        if (!shiroUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "list")
            return
        }

        [shiroUserInstance: shiroUserInstance]
    }

    def edit() {
        def shiroUserInstance = ShiroUser.get(params.id)
        if (!shiroUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "list")
            return
        }

        [shiroUserInstance: shiroUserInstance]
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

        shiroUserInstance.properties = params
        shiroUserInstance.properties.put('permissions', params.get('permissions').toString().replace('[', '').replace(']', '').split(','))

        if (!shiroUserInstance.save(flush: true)) {
            render(view: "/user/edit", model: [shiroUserInstance: shiroUserInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), shiroUserInstance.id])
        redirect(action: "show", id: shiroUserInstance.id)
    }

    def delete() {
        def shiroUserInstance = ShiroUser.get(params.id)
        if (!shiroUserInstance) {
            flash.message = message(code: 'default.not.found.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "list")
            return
        }

        try {
            shiroUserInstance.delete(flush: true)
            flash.message = message(code: 'default.deleted.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "list")
        }
        catch (DataIntegrityViolationException e) {
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'shiroUser.label', default: 'ShiroUser'), params.id])
            redirect(action: "show", id: params.id)
        }
    }
}
