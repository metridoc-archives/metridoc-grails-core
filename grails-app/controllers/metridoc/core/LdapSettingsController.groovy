package metridoc.core

class LdapSettingsController {

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def encryptionService

    def index() {
        def testLdap = LdapData.findByName("temp")
        def oldLdapConfig = LdapData.findByName("ldapConfig")
        def ldapConfig = new LdapData(
                name: "temp",
                server: oldLdapConfig.server,
                rootDN: oldLdapConfig.rootDN,
                userSearchBase: oldLdapConfig.userSearchBase,
                userSearchFilter: oldLdapConfig.userSearchFilter,
                groupSearchBase: oldLdapConfig.groupSearchBase,
                managerDN: oldLdapConfig.managerDN,
                managerPassword: oldLdapConfig.managerPassword,
                encryptStrong: oldLdapConfig.encryptStrong
        )
        ldapConfig.managerPassword = encryptionService.decryptString(ldapConfig.managerPassword, ldapConfig.encryptStrong)
        ldapConfig.save()
        render(view: "index", model: [LDAP: ldapConfig])
    }

    def save() {

        def tempLdapConfig = LdapData.findByName("temp")
        if (tempLdapConfig) tempLdapConfig.delete()
        def newLdapConfig = LdapData.findByName("ldapConfig")
        if (newLdapConfig) {
            newLdapConfig.server = params.server
            newLdapConfig.rootDN = params.rootDN
            newLdapConfig.userSearchBase = params.userSearchBase
            newLdapConfig.userSearchFilter = params.userSearchFilter
            newLdapConfig.groupSearchBase = params.groupSearchBase
            newLdapConfig.managerDN = params.managerDN
            newLdapConfig.managerPassword = params.managerPassword
        } else {
            newLdapConfig = new LdapData(
                    name: "ldapConfig",
                    server: params.server,
                    rootDN: params.rootDN,
                    userSearchBase: params.userSearchBase,
                    userSearchFilter: params.userSearchFilter,
                    groupSearchBase: params.groupSearchBase,
                    managerDN: params.managerDN,
                    managerPassword: params.managerPassword
            )
        }
        if (params.encryptStrong == "true") newLdapConfig.encryptStrong = true
        else newLdapConfig.encryptStrong = false
        newLdapConfig.save(failOnError: true)
        newLdapConfig.managerPassword = encryptionService.encryptString(params.managerPassword, newLdapConfig.encryptStrong)
        newLdapConfig.save(failOnError: true)
        flash.message = "LDAP configuration updated"
        redirect(action: "index", model: [LDAP: newLdapConfig])


    }
}

