/*
  *Copyright 2013 Trustees of the University of Pennsylvania. Licensed under the
  *	Educational Community License, Version 2.0 (the "License"); you may
  *	not use this file except in compliance with the License. You may
  *	obtain a copy of the License at
  *
  *http://www.osedu.org/licenses/ECL-2.0
  *
  *	Unless required by applicable law or agreed to in writing,
  *	software distributed under the License is distributed on an "AS IS"
  *	BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
  *	or implied. See the License for the specific language governing
  *	permissions and limitations under the License.  */

package metridoc.core

import org.apache.shiro.authc.AccountException
import org.apache.shiro.authc.CredentialsException
import org.apache.shiro.authc.IncorrectCredentialsException
import org.apache.shiro.authc.UnknownAccountException
import org.apache.shiro.authc.UsernamePasswordToken

import javax.naming.AuthenticationException
import javax.naming.Context
import javax.naming.NamingException
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls

/**
 * Simple realm that authenticates users against an LDAP server.
 */
class ShiroLdapRealm {
    static authTokenClass = UsernamePasswordToken
    static LOCALHOST_LDAP = "ldap://localhost:389/"
    public static final int RECURSIVE_SEARCH_SCOPE = 2
    def grailsApplication
    def roleMappingService

    def authenticate(authToken) {
        log.info "Attempting to authenticate ${authToken.username} in LDAP realm..."
        def username = authToken.username
        def password = new String(authToken.password)
        def appConfig = LdapData.list().get(0)

        // Skip authentication ?
        if (appConfig.skipAuthentication) {
            log.info "Skipping authentication in development mode."
            return username
        }

        // Null username is invalid
        if (username == null) {
            throw new AccountException("Null usernames are not allowed by this realm.")
        }

        // Empty username is invalid
        if (username == "") {
            throw new AccountException("Empty usernames are not allowed by this realm.")
        }

        // Allow empty passwords ?
        if (!appConfig.allowEmptyPasswords) {
            // Null password is invalid
            if (password == null) {
                throw new CredentialsException("Null password are not allowed by this realm.")
            }

            // empty password is invalid
            if (password == "") {
                throw new CredentialsException("Empty passwords are not allowed by this realm.")
            }
        }

        // Accept strings and GStrings for convenience, but convert to
        // a list.
        def ldapUrl = appConfig.server

        // Set up the configuration for the LDAP search we are about
        // to do.
        def env = new Hashtable()
        env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
        if (appConfig.managerDN) {
            // Non-anonymous access for the search.
            env[Context.SECURITY_AUTHENTICATION] = "simple"
            env[Context.SECURITY_PRINCIPAL] = appConfig.managerDN
            env[Context.SECURITY_CREDENTIALS] = appConfig.unencryptedPassword
        }

        // Find an LDAP server that we can connect to.
        def ctx
        log.info "Trying to connect to LDAP server ${ldapUrl} ..."
        env[Context.PROVIDER_URL] = ldapUrl

        // If an exception occurs, log it.
        try {
            ctx = new InitialDirContext(env)
        }
        catch (NamingException e) {
            if (ldapUrl != LOCALHOST_LDAP) {
                log.error "Could not connect to ${ldapUrl}: ${e}"
            }
            def msg = 'Could not connect to LDAP server'
            log.warn msg
            throw new org.apache.shiro.authc.AuthenticationException(msg)
        }

        // Look up the DN for the LDAP entry that has a 'uid' value
        // matching the given username.
        SearchControls searchControls = new SearchControls()

        searchControls.setSearchScope(RECURSIVE_SEARCH_SCOPE)
        String filter = "($appConfig.userSearchFilter=$username)"

        def base = "$appConfig.userSearchBase,$appConfig.rootDN"
        def result = ctx.search(base, filter, searchControls)
        if (!result.hasMore()) {
            throw new UnknownAccountException("No account found for user [${username}]")
        }

        // Skip credentials check ?
        if (appConfig.skipCredentialsCheck) {
            log.info "Skipping credentials check in development mode."
            return username
        }

        // Now connect to the LDAP server again, but this time use
        // authentication with the principal associated with the given
        // username.
        def searchResult = result.next()
        env[Context.SECURITY_AUTHENTICATION] = "simple"
        env[Context.SECURITY_PRINCIPAL] = searchResult.nameInNamespace
        env[Context.SECURITY_CREDENTIALS] = password

        try {
            new InitialDirContext(env)
            return username
        }
        catch (AuthenticationException ignored) {
            log.info "Invalid password"
            throw new IncorrectCredentialsException("Invalid password for user '${username}'")
        }
    }

    protected static List getParams(LdapData appConfig) {

        def ldapUrl = appConfig.server ?: [LOCALHOST_LDAP]
        def searchBase = appConfig.userSearchBase
        def searchUser = appConfig.rootDN
        def searchPass = appConfig.unencryptedPassword
        def searchScope = 2
        def usernameAttribute = appConfig.userSearchFilter
        def skipAuthc = appConfig.skipAuthentication
        def skipCredChk = appConfig.skipCredentialsCheck
        def allowEmptyPass = appConfig.allowEmptyPasswords
        [skipAuthc, allowEmptyPass, ldapUrl, searchUser, searchPass, searchScope, usernameAttribute, searchBase, skipCredChk]
    }

    def isAdmin(principal) {
        def adminRole = ShiroRole.findByName("ROLE_ADMIN")
        def groups = roleMappingService.userGroupsAsList(principal)
        if (!groups) return false
        def roles = roleMappingService.rolesByGroups(groups)
        return roles.contains(adminRole.name)
    }

    def hasRole(principal, roleName) {

        def groups = roleMappingService.userGroupsAsList(principal)
        if (!groups) return false
        def roles = roleMappingService.rolesByGroups(groups)
        return roles?.contains(roleName)
    }

    def hasAllRoles(principal, roleList) {
        boolean allRoles = true

        if (isAdmin(principal)) {
            return true
        }

        def groups = roleMappingService.userGroupsAsList(principal as String)
        if (!groups) return false
        def roles = roleMappingService.rolesByGroups(groups)
        for (role in roleList) {
            if (!roles.contains(role)) {
                allRoles = false
            }
        }
        return allRoles
    }
}
