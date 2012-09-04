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

import org.apache.shiro.SecurityUtils
import org.apache.shiro.authc.AuthenticationException
import org.apache.shiro.authc.UsernamePasswordToken
import org.apache.shiro.web.util.SavedRequest
import org.apache.shiro.web.util.WebUtils
import metridoc.reports.ShiroUser
import org.apache.shiro.crypto.hash.Sha256Hash

class AuthController {
    def shiroSecurityManager
    def mailService
    def authService
    def grailsApplication
    def resetIdByUserName = [:]

    def index() {
        if (!params.template) {
            [template: 'login', username: params.username, rememberMe: (params.rememberMe != null), targetUri: params.targetUri]
        } else {
            [params: params]
        }
    }

    def login(){
        render(view: 'index', model: [template: 'login', username: params.username, rememberMe: (params.rememberMe != null), targetUri: params.targetUri])
    }

    def unauthorized(){
        render(view: 'index', model: [template: 'unauthorized'])
    }

    def forgetPassword(){
        flash.message = "Please enter your email address you used for registration below"
        render(view: 'index', model: [template: 'forgetPassword'])
    }

    def resetPassword(){
        if (params.emailAddress) {
            flash.message = "Thank you! An email providing an access to reset your password has been sent."
            render(view: 'index', model: [template: 'forgetPassword'])

            def id = authService.addResetLink()
            def link = grailsApplication.config.grails.serverURL + "/auth/doResetPassword?id=${id}"
            def user = ShiroUser.findAllByEmailAddress(params.emailAddress)
            if (user) {
                mailService.sendMail {
                    to "${params.emailAddress}"
                    subject "Reset Password"
                    body "Go here to reset your password: ${link}"
                }
                resetIdByUserName[id] = user
            }
        } else {
            flash.message = "Please offer your email address you used for registration below"
            render(view: 'index', model: [template: 'forgetPassword'])
        }

    }

    def doResetPassword(){
        def id = params.id
        def resetId = params.resetPasswordId
        def method = request.method

        switch(method) {
            case "POST":
                def user = resetIdByUserName[resetId as Integer]
                def password = params.password
                if (user && password) {
                    log.info "reseting password for ${user.username}"
                    user.setPassword(new Sha256Hash(password).toHex())
                    user.save(flush:true)
                    def authToken = new UsernamePasswordToken(user.username, password)
                    SecurityUtils.subject.login(authToken)
                } else {
                    if(!password) {
                        log.info "tried to reset password but password was null, available params are ${params}"
                    }

                    if (!user) {
                        log.info "tried to reset password but user was null, available users are ${resetIdByUserName}"
                    }
                }

                chain(controller: "home", action: "index")
                break;
            case "GET":
                if(id && authService.canReset(id as Integer)) {
                    render(view: 'index', model: [template: 'doResetPassword', resetPasswordId: id])
                } else {
                    chain(action: "signIn")
                }
                break;
        }
    }

    def signIn (){
        def authToken = new UsernamePasswordToken(params.username, params.password as String)

        // Support for "remember me"
        if (params.rememberMe) {
            authToken.rememberMe = true
        }

        // If a controller redirected to this page, redirect back
        // to it. Otherwise redirect to the root URI.
        def targetUri = params.targetUri ?: "/"

        // Handle requests saved by Shiro filters.
        def savedRequest = WebUtils.getSavedRequest(request)
        if (savedRequest) {
            targetUri = savedRequest.requestURI - request.contextPath
            if (savedRequest.queryString) targetUri = targetUri + '?' + savedRequest.queryString
        }

        try {
            // Perform the actual login. An AuthenticationException
            // will be thrown if the username is unrecognised or the
            // password is incorrect.
            SecurityUtils.subject.login(authToken)

            log.info "Redirecting to '${targetUri}'."
            redirect(uri: targetUri)
        }
        catch (AuthenticationException ex) {
            // Authentication failed, so display the appropriate message
            // on the login page.
            log.info "Authentication failure for user '${params.username}'."
            flash.message = message(code: "login.failed")

            // Keep the username and "remember me" setting so that the
            // user doesn't have to enter them again.
            def m = [username: params.username]
            if (params.rememberMe) {
                m["rememberMe"] = true
            }

            // Remember the target URI too.
            if (params.targetUri) {
                m["targetUri"] = params.targetUri
            }

            // Now redirect back to the login page.
            redirect(action: "login", params: m)
        }
    }

    def signOut(){
        // Log the user out of the application.
        SecurityUtils.subject?.logout()

        // For now, redirect back to the home page.
        redirect(uri: "/")
    }

}
