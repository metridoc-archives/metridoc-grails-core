package metridoc.core

class LdapSettingsController {

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def encryptionService

    def index() {
        chain(controller: "ldapRole", action: "index")
    }

    def save() {
        def newLdapConfig
        if (LdapData.list().size() > 0) {
            newLdapConfig = LdapData.list().get(0)
            newLdapConfig.server = params.server
            newLdapConfig.rootDN = params.rootDN
            newLdapConfig.userSearchBase = params.userSearchBase
            newLdapConfig.userSearchFilter = params.userSearchFilter
            newLdapConfig.managerDN = params.managerDN
        } else {
            newLdapConfig = new LdapData(
                    server: params.server,
                    rootDN: params.rootDN,
                    userSearchBase: params.userSearchBase,
                    userSearchFilter: params.userSearchFilter,
                    managerDN: params.managerDN,
            )
        }
        encryptionService.encryptString(newLdapConfig, params.unencryptedPassword)
        newLdapConfig.save(failOnError: true)
        flash.alert = "LDAP configuration updated"
        session.setAttribute("previousExpanded", "ldapConfig")
        chain(controller: "ldapRole", action: "index")
    }
}
