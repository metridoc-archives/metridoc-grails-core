package metridoc.admin

import org.apache.commons.lang.math.RandomUtils
import metridoc.reports.ShiroUser
import org.apache.shiro.crypto.hash.Sha256Hash
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.SecurityUtils

class AuthService {

    def mailService
    def grailsApplication
    /*A table of Time when the ResetPassword link is generated corresponding to each ResetPassword ID*/
    def dateById = [:]
    public static final FIFTEEN_MINUTES = 1000 * 60 * 15
    //
    def resetableUserById = [:]

    def addUserById(id, user) {
        resetableUserById[id] = user
    }

    def getUserById(id) {
        resetableUserById.remove(id)
    }

    def canReset(id) {
        canReset(id, new Date().getTime())
    }
    private canReset(id, now) {
        def date = dateById.remove(id)

        if (date) {
            def validTime = now < (date.time + FIFTEEN_MINUTES)
            def dateNotNull = date != null
            def canReset = dateNotNull && validTime

            return canReset
        }

        return false
    }

    def addResetLink() {
        def id = RandomUtils.nextInt()
        dateById[id] = new Date()
        return id
    }

    def sendResetPasswordEmail(String emailAddress){
        def id = addResetLink()
        def link = grailsApplication.config.grails.serverURL + "/auth/doResetPassword?id=${id}"
        def user = ShiroUser.findByEmailAddress(emailAddress)
        if (user) {
            mailService.sendMail {
                to "${emailAddress}"
                subject "Reset Password"
                body "Go here to reset your password: ${link}"
            }
            addUserById(id, user)
        }
    }

    boolean isPasswordValid(String password){
        return (password.length() >= 5 && password.length() <= 15)
    }

    boolean isPasswordMatch(String password, String confirm){
        return password == confirm
    }


    def newResetLink(user){
        def id = addResetLink()
        addUserById(id, user)
        def link = grailsApplication.config.grails.serverURL + "/auth/doResetPassword?id=${id}"
        return link
    }

    def resetPassword(user, password, confirm){
        def passwordHash = new Sha256Hash(password).toHex()
        log.info "reseting password for ${user.username}"
        ShiroUser.withTransaction {
            def userToUpdate = ShiroUser.findByUsername(user.username)
            userToUpdate.password = password
            userToUpdate.confirm = confirm
            userToUpdate.passwordHash = passwordHash
            userToUpdate.save(flush: true)
        }
        def authToken = new UsernamePasswordToken(user.username, password as String)
        SecurityUtils.subject.login(authToken)
    }
}