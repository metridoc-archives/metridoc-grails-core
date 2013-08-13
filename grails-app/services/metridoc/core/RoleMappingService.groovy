package metridoc.core

import javax.naming.Context
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls

class RoleMappingService {


    def userGroupsAsList(String targetUser) {

/**
 * Created with IntelliJ IDEA on 7/18/13
 * @author Tommy Barker
 */
        def ldapSettings = LdapData.list()
        if (ldapSettings) ldapSettings = ldapSettings.get(0)
        else return null
        def config = new ConfigSlurper().parse(new File("${System.getProperty("user.home")}/.metridoc/MetridocConfig.groovy").toURI().toURL())
        def url
        def searchBase
        def username
        def pass
        def searchScope
        def usernameAttribute
        SearchControls searchControls
        def env
        def ctx

        try {
            url = ldapSettings.server
            searchBase = ldapSettings.rootDN
            username = ldapSettings.userSearchBase
            pass = ldapSettings.unencryptedPassword
            searchScope = 2
            usernameAttribute = ldapSettings.userSearchFilter
            searchControls = new SearchControls()
            searchControls.setSearchScope(searchScope)

            env = new Hashtable()
            env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
// Non-anonymous access for the search.
            env[Context.SECURITY_AUTHENTICATION] = "simple"
            env[Context.SECURITY_PRINCIPAL] = username
            env[Context.SECURITY_CREDENTIALS] = pass
            env[Context.PROVIDER_URL] = url

            ctx = new InitialDirContext(env)
        } catch (Exception ex) {
            return null
        }
        String filter = "($usernameAttribute=${targetUser})"

        def result = ctx.search(searchBase, filter, searchControls)
//groups
        def groups = new ArrayList()
        result.each {
            it.getAttributes().get("memberOf").all.each {
                def getCN = it.split(",")[0].split("CN=")[1]
                groups.add(getCN)
            }
        }
        return groups
    }

    def rolesByGroups(groups) {
        def roles = new ArrayList()
        def roleName
        def query = LdapRoleMapping.where {
            groups.contains(name)
        }.list()

        for (group in query) {
            for (role in group.roles) {
                if (!roles.contains(role.name) && role != null && role?.name != "") {
                    roles.add(role.name)
                }
            }
        }
        return roles
    }

    def allGroups() {
        def ldapSettings = LdapData.list()
        if (ldapSettings) ldapSettings = ldapSettings.get(0)
        else return null
        def config = new ConfigSlurper().parse(new File("${System.getProperty("user.home")}/.metridoc/MetridocConfig.groovy").toURI().toURL())
        def url
        def searchBase
        def username
        def pass
        def searchScope
        def usernameAttribute
        SearchControls searchControls
        def env
        def ctx


        try {
            url = ldapSettings.server
            searchBase = ldapSettings.rootDN
            username = ldapSettings.userSearchBase
            pass = ldapSettings.unencryptedPassword
            searchScope = 2
            usernameAttribute = ldapSettings.userSearchFilter
            searchControls = new SearchControls()
            searchControls.setSearchScope(searchScope)

            env = new Hashtable()
            env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
// Non-anonymous access for the search.
            env[Context.SECURITY_AUTHENTICATION] = "simple"
            env[Context.SECURITY_PRINCIPAL] = username
            env[Context.SECURITY_CREDENTIALS] = pass
            env[Context.PROVIDER_URL] = url


            ctx = new InitialDirContext(env)
        } catch (Exception ex) {
            return null
        }

        String[] attrIDs = { "cn" };
        def allGroups = new ArrayList()
        def filter = "(objectclass=group)"
        def result = ctx.search(searchBase, filter, searchControls);
        result.each {
            def attrs = it.getAttributes();
            allGroups.add(attrs.get("cn").toString().replace("cn: ", ""));
        }
        return allGroups
    }

    def isValidGroup(groupName) {
        return allGroups()?.contains(groupName)
    }
}
