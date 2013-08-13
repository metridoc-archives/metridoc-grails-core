package metridoc.core

import javax.naming.Context
import javax.naming.directory.InitialDirContext
import javax.naming.directory.SearchControls

class RoleMappingService {

    def ldapSettings = LdapData.list().get(0)

    def userGroupsAsList(String targetUser) {

/**
 * Created with IntelliJ IDEA on 7/18/13
 * @author Tommy Barker
 */
        def config = new ConfigSlurper().parse(new File("${System.getProperty("user.home")}/.metridoc/MetridocConfig.groovy").toURI().toURL())
        def url = ldapSettings.server
        def searchBase = ldapSettings.rootDN
        def username = ldapSettings.userSearchBase
        def pass = ldapSettings.unencryptedPassword
        def searchScope = 2
        def usernameAttribute = ldapSettings.userSearchFilter
        SearchControls searchControls = new SearchControls()
        searchControls.setSearchScope(searchScope)

        def env = new Hashtable()
        env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
// Non-anonymous access for the search.
        env[Context.SECURITY_AUTHENTICATION] = "simple"
        env[Context.SECURITY_PRINCIPAL] = username
        env[Context.SECURITY_CREDENTIALS] = pass
        env[Context.PROVIDER_URL] = url

        def ctx = new InitialDirContext(env)

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

        def config = new ConfigSlurper().parse(new File("${System.getProperty("user.home")}/.metridoc/MetridocConfig.groovy").toURI().toURL())
        def url = ldapSettings.server
        def searchBase = ldapSettings.rootDN
        def username = ldapSettings.userSearchBase
        def pass = ldapSettings.unencryptedPassword
        def searchScope = 2
        def usernameAttribute = ldapSettings.userSearchFilter
        SearchControls searchControls = new SearchControls()
        searchControls.setSearchScope(searchScope)

        def env = new Hashtable()
        env[Context.INITIAL_CONTEXT_FACTORY] = "com.sun.jndi.ldap.LdapCtxFactory"
// Non-anonymous access for the search.
        env[Context.SECURITY_AUTHENTICATION] = "simple"
        env[Context.SECURITY_PRINCIPAL] = username
        env[Context.SECURITY_CREDENTIALS] = pass
        env[Context.PROVIDER_URL] = url

        def ctx = new InitialDirContext(env)

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
        return allGroups().contains(groupName)
    }
}
