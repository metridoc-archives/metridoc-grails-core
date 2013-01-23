package metridoc.core

import metridoc.reports.ShiroUser
import org.apache.shiro.SecurityUtils
import org.apache.shiro.crypto.hash.Sha256Hash


class ProfileController {

    static boolean isProtected = true
    static allowedMethods = [save: "POST", update: "POST", index: "GET"]


    def index() {
        chain(action: "edit")
    }

    def edit() {
        //TODO: remove after debugging
        log.info params
        def currentUser = SecurityUtils.getSubject().getPrincipal()
        ShiroUser shiroUserInstance = ShiroUser.findByUsername(currentUser)

        if (shiroUserInstance.username == 'anonymous') {
            flash.message = message(code: 'cannot.modify.message', args: ['Anonymous User'], default: 'Anonymous User cannot be modified.')
        } else if (params.flashMessage) {
            flash.message = params.flashMessage
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
                render(view: "/profile/edit", model: [shiroUserInstance: shiroUserInstance])
                return
            }
        }

        if (params.changePW) {
            log.info "password and user details for ${shiroUserInstance.username} are changing"
            updateUserInfoAndPassword(shiroUserInstance, params)
            def userInputCurrentPassword = params.get('oldPassword')
            if (!shiroUserInstance.passwordHash.equals(new Sha256Hash(userInputCurrentPassword).toHex())) {
                redirect(action: "edit", params: [flashMessage: 'Current Password is not correct'])
                return
            }
            if (params.get('password') != params.get('confirm')) {
                redirect(action: "edit", params: [flashMessage:"New passwords don't match"])
                return
            }
        } else {
            log.info "user details for ${shiroUserInstance.username} are changing, password will remain the same"
            updateUserInfo(shiroUserInstance, params)
        }

        if (!shiroUserInstance.save(flush: true)) {
            if (shiroUserInstance.errors) {
                log.error("There were errors trying to update ${shiroUserInstance}")
                shiroUserInstance.errors.each {error ->
                    log.error("Error ${error} occurred trying to update user ${shiroUserInstance}")
                }
            }
            redirect(action: "edit", params: [flashMessage: "There were errors trying to update user details"])
            return
        }

        def flashMessage = "User details updated"
        redirect(action: "edit", params: [flashMessage:flashMessage])
    }

    static protected updateUserInfo(ShiroUser user, params) {
        user.with {
            emailAddress = params.emailAddress ?: params.emailAddress
        }
    }

    static protected updateUserInfoAndPassword(ShiroUser user, params) {
        updateUserInfo(user, params)
        user.with {
            password = params.password
            confirm = params.confirm
            user.setPasswordHash(new Sha256Hash(password).toHex())
        }
    }

}
