package metridoc.admin

import metridoc.reports.ShiroUser
import org.apache.shiro.SecurityUtils
import org.apache.shiro.crypto.hash.Sha256Hash


class ChangePasswordController {

    static allowedMethods = [save: "POST", update: "POST", index: "GET"]


    def index() {
        chain(action: "edit")
    }

    def edit() {
        def currentUser = SecurityUtils.getSubject().getPrincipal()
        def shiroUserInstance = ShiroUser.findByUsername(currentUser)

        if (shiroUserInstance.getUsername() == 'anonymous') {
            flash.message = message(code: 'cannot.modify.message', args: ['Anonymous User'], default: 'Anonymous User cannot be modified.')
        }
        [
                shiroUserInstance: shiroUserInstance
        ]
    }


    def update() {

        def shiroUserInstance = ShiroUser.get(params.id)

        if (params.version) {
            def version = params.version.toLong()
            if (shiroUserInstance.version > version) {
                shiroUserInstance.errors.rejectValue("version", "default.optimistic.locking.failure",
                        [message(code: 'shiroUser.label', default: 'User')] as Object[],
                        "Another user has updated this ShiroUser while you were editing")
                render(view: "/changePassword/edit", model: [shiroUserInstance: shiroUserInstance])
                return
            }
        }

        if (params.get('password') != params.get('confirm')) {
            shiroUserInstance.errors.rejectValue('password', '',"Passwords don't match")
            render(view: "/changePassword/edit", model: [shiroUserInstance: shiroUserInstance])
            return
        }

        shiroUserInstance.with {
            username =  SecurityUtils.getSubject().getPrincipal()
            password = params.password
            confirm = params.confirm
            shiroUserInstance.setPasswordHash(new Sha256Hash(password).toHex())
        }

        if (!shiroUserInstance.save(flush: true)) {
            if (shiroUserInstance.errors) {
                log.error("There were errors trying to update ${shiroUserInstance}")
                shiroUserInstance.errors.each {error ->
                    log.error("Error ${error} occurred trying to update user ${shiroUserInstance}")
                }
            }
            render(view: "/changePassword/edit", model: [shiroUserInstance: shiroUserInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'shiroUser.label', default: 'User'), SecurityUtils.getSubject().principal])
        redirect(controller: 'home', action: 'index', params: flash.message)
    }

}
