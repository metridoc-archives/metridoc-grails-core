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

class UserController {
    final static DEFAULT_PASSWORD = "password"
    static allowedMethods = [save: "POST", update: "POST", delete: "POST"]
    def static final reportName = "Manage Users"
    def static adminOnly = true

    def index(){
        redirect(action: 'list', params: params)
    }

    def list() {
        params.max = Math.min(params.max ? params.int('max') : 10, 100)
        [shiroUserInstanceList: ShiroUser.list(params), shiroUserInstanceTotal: ShiroUser.count()]
    }

    def create() {
        [shiroUserInstance: new ShiroUser(params)]
    }

    def save() {
        def preConfiguredPassword = params.get('passwordHash')
        def password = preConfiguredPassword ? preConfiguredPassword : DEFAULT_PASSWORD

        if (DEFAULT_PASSWORD == password) {
            log.warn "Creating a user with default password '${DEFAULT_PASSWORD}'.  Change this immediatelly"
        }
        def shiroUserInstance = new ShiroUser(username: params.get('username'), passwordHash: new Sha256Hash(params.get('passwordHash')).toHex(), emailAddress: params.get('emailAddress'))
        def permissions = params.get('permissions').toString().split(',')
        permissions.each {
            shiroUserInstance.addToPermissions("${it}")
        }

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
        shiroUserInstance.properties.put('permissions',params.get('permissions').toString().replace('[','').replace(']','').split(','))

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
