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
        def password = params.get('password')
        def pwConfirm = params.get('confirm')
        if (password != pwConfirm) {
            flash.message = message(code: 'user.password.dontmatch', default: 'Passwords not match')
            render(view: "/changePassword/edit")
            return
        }

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

        shiroUserInstance.setPasswordHash(new Sha256Hash(password).toHex())
        if (!shiroUserInstance.save(flush: true)) {
            render(view: "/changePassword/edit", model: [shiroUserInstance: shiroUserInstance])
            return
        }

        flash.message = message(code: 'default.updated.message', args: [message(code: 'shiroUser.label', default: 'User'), shiroUserInstance.username])
        render(view: '/changePassword/edit', model: [shiroUserInstance: shiroUserInstance])
    }

}
