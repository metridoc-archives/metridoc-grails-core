package metridoc.core
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
import org.apache.shiro.web.util.WebUtils

class AuthController {

    def authService
    def grailsApplication

    def index() {
        def model
        if (!params.template) {
            model = [template: 'login', username: params.username, rememberMe: (params.rememberMe != null), targetUri: params.targetUri]
        } else {
            model = [params: params]
        }

        def mailIsSet = grailsApplication.config.grails.mail.username
        if(mailIsSet) {
            model.forgotPassword = true
        }

        return model
    }

    def login() {
        render(view: 'index', model: [template: 'login', username: params.username, rememberMe: (params.rememberMe != null), targetUri: params.targetUri])
    }

    def unauthorized() {
        render(view: 'index', model: [template: 'unauthorized'])
    }

    def forgetPassword() {
        render(view: 'index', model: [template: 'forgetPassword'])
    }

    def resetPassword() {
        def email = params.emailAddress
        if (email) {
            authService.sendResetPasswordEmail(email);
            request.message = "An email with a reset link has been sent to ${email}"
            render(view: 'index', model: [template: 'forgetPassword', hideInput: true])
        } else {
            render(view: 'index', model: [template: 'forgetPassword'])
        }

    }

    def doResetPassword() {
        def id = params.id
        def resetId = params.resetPasswordId
        def method = request.method
        switch (method) {
            case "POST":
                def user = authService.getUserById(resetId as Integer)
                def password = params.password
                def confirm = params.confirm
                if (user && password) {

                    if (!authService.isPasswordValid(password.toString())) {
                        def link = authService.newResetLink(user)
                        flash.message = "Password length must be within 5-15"
                        redirect(url: link, params: [resetPasswordId: id])
                        return
                    }
                    if (!authService.isPasswordMatch(password.toString(), confirm.toString())) {//generate a new reset password id and link
                        def link = authService.newResetLink(user)
                        flash.message = "Passwords don't match"
                        redirect(url: link, params: [resetPasswordId: id])
                        return
                    }

                    authService.resetPassword(user, password as String, confirm as String);

                } else {
                    if (!password) {
                        log.warn "tried to reset password but password was null, available params are ${params}"
                    }
                    if (!user) {
                        log.warn "tried to reset password but user was null, available users are ${authService.resetableUserById}"
                    }
                }
                chain(controller: "home", action: "index")
                break;
            case "GET":
                try {
                    if (id && authService.canReset(id as Integer)) {
                        render(view: 'index', model: [template: 'doResetPassword', resetPasswordId: id])
                    } else {
                        redirect(action: "index")
                    }
                } catch (NumberFormatException ne) {
                    redirect(action: "index")
                }
                break;
        }
    }

    def signIn() {
        def authToken = new UsernamePasswordToken(params.username, params.password as String)

        // Support for "remember me"
        if (params.rememberMe) {
            authToken.rememberMe = true
        }

        // If a controller redirected to this page, redirect back
        // to it. Otherwise redirect to the root URI.
        def targetUri = params.targetUri

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

            if (targetUri) {
                log.info "Redirecting to '${targetUri}'."
                redirect(uri: targetUri)
            } else {
                redirect(controller: "home")
            }
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

    def signOut() {
        // Log the user out of the application.
        SecurityUtils.subject?.logout()

        // For now, redirect back to the home page.
        redirect(controller: "home")
    }

}
