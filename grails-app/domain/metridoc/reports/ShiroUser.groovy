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
package metridoc.reports

import org.apache.commons.lang.StringUtils
import org.apache.shiro.crypto.hash.Sha256Hash

/**
 * Represents the user of the application.
 */
class ShiroUser {
    Boolean validatePasswords
    String username
    String passwordHash
    String password
    String oldPassword
    String confirm
    String emailAddress
    String passwordErrorMessage
    static transients = ['password', 'confirm', 'oldPassword', 'validatePasswords', 'passwordErrorMessage']
    static final EMAIL_ERROR = { String emailAddress ->
        "The email ${emailAddress} is associated with another account, please choose another"
    }

    static final FIELD_CANNOT_BE_NULL_OR_BLANK = {String fieldName ->
        "${fieldName} cannot be null or empty"
    }
    static final NOT_A_VALID_EMAIL = { String email ->
        "'${email}' is not a valid email address"
    }

    static final PASSWORD_MISMATCH = "Password must be longer than 5 characters"
    static final OLD_PASSWORD_MISMATCH = "Current password is not correct"
    static final CONFIRM_MISMATCH = "Confirm password does not match"
    static final PASSWORD_IS_INVALID = "Password is invalid"
    static final USERNAME_IS_NOT_UNIQUE = "User name is associatted with another account, please choose a different one"
    static final NOT_A_VALID_USERNAME = {String username ->
        "'${username} is not a valid user name"
    }
    static final ADMIN_MUST_HAVE_ROLE_ADMIN = "admin must have [ROLE_ADMIN] as a role"
    static final ROLES_ARE_NOT_VALID = "roles are not valid"
    static mapping = {

    }

    static hasMany = [roles: ShiroRole, permissions: String]

    static constraints = {
        username(nullable: false, blank: false, unique: true)
        emailAddress(email: true, blank: false, unique: true)
        passwordHash(blank: false, validator: { val, obj ->
            Boolean validatePasswords = obj.validatePasswords
            if (validatePasswords) {
                if (obj.password == null || obj.password == StringUtils.EMPTY) {
                    obj.passwordErrorMessage = FIELD_CANNOT_BE_NULL_OR_BLANK("password")
                    return "password.nullable"
                }
                if (obj.password.size() < 5) return "password.match"
                if (obj.oldPassword) {
                    def hash = new Sha256Hash(obj.oldPassword)
                    if (hash != obj.passwordHash) return "oldPassword.match"
                }
                if (obj.confirm) {
                    if (obj.confirm != obj.password) return "confirm.match"
                }
            }

            return true
        })
        roles(validator: {val, obj ->
            if ("admin" == obj.username) {
                boolean containsAdminRole = false
                val.each {role ->
                    if ("ROLE_ADMIN" == role.name) {
                        containsAdminRole = true
                    }
                }
                if(!containsAdminRole) {
                    return "role.noAdminForAdmin"
                }
            }
        })
    }

    static addAlertForAllErrors(ShiroUser user, Map flash) {
        addAlertsForEmailErrors(user, flash)
        addAlertForPasswordErrors(user, flash)
        addAlertForUserNameErrors(user, flash)
        addAlertsForRoleErrors(user, flash)
    }

    static addAlertsForRoleErrors(ShiroUser user, Map flash) {
        user.errors.getFieldError("roles").each {
            switch(it.code) {
                case "role.noAdminForAdmin":
                    flash.alert = ADMIN_MUST_HAVE_ROLE_ADMIN
                    break
                default:
                    flash.alert = ROLES_ARE_NOT_VALID
            }
        }
    }

    static addAlertForUserNameErrors(ShiroUser user, Map flash) {
        user.errors.getFieldError("username").each {
            switch(it.code) {
                case "unique":
                    flash.alert = USERNAME_IS_NOT_UNIQUE
                    break
                case "nullable":
                case "blank":
                    flash.alert = FIELD_CANNOT_BE_NULL_OR_BLANK.call("username")
                    break
                default:
                    flash.alert = NOT_A_VALID_USERNAME(user.username)
            }
        }
    }

    static addAlertsForEmailErrors(ShiroUser user, Map flash) {
        user.errors.getFieldErrors("emailAddress").each {
            switch (it.code) {
                case "unique":
                    flash.alert = EMAIL_ERROR.call(user.emailAddress)
                    break;
                case "nullable":
                case "blank":
                    flash.alert = FIELD_CANNOT_BE_NULL_OR_BLANK.call("email")
                    break
                //for all other errors just say it is invalid
                default:
                    flash.alert = NOT_A_VALID_EMAIL.call(user.emailAddress)
                    break
            }
        }
    }

    private static addAlertForPasswordErrors(ShiroUser user, Map flash) {
        user.errors.getFieldErrors("passwordHash").each {
            switch (it.code) {
                case "password.nullable":
                    flash.alert = FIELD_CANNOT_BE_NULL_OR_BLANK.call("password")
                    break
                case "password.match":
                    flash.alert = PASSWORD_MISMATCH
                    break
                case "oldPassword.match":
                    flash.alert = OLD_PASSWORD_MISMATCH
                    break
                case "confirm.match":
                    flash.alert = CONFIRM_MISMATCH
                    break
                    //for all other errors just say it is invalid
                default:
                    flash.alert = PASSWORD_IS_INVALID
                    break
            }
        }
    }
}
