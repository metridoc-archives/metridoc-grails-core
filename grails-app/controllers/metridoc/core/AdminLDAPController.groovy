package metridoc.core

import org.jasypt.util.text.StrongTextEncryptor

class AdminLDAPController {

    static accessControl = {
        role(name: "ROLE_ADMIN")
    }

    def index() {
        def old_LDAP_Config = LDAP_Data.findByName("LDAP_Config")
        def LDAP_Config = new LDAP_Data(
                name: "temp",
                server: old_LDAP_Config.server,
                rootDN: old_LDAP_Config.rootDN,
                userSearchBase: old_LDAP_Config.userSearchBase,
                userSearchFilter: old_LDAP_Config.userSearchFilter,
                groupSearchBase: old_LDAP_Config.groupSearchBase,
                managerDN: old_LDAP_Config.managerDN,
                managerPassword: old_LDAP_Config.managerPassword
        )
        StrongTextEncryptor textEncrypt = new StrongTextEncryptor()
        textEncrypt.setPassword(MKey.list().get(0).encryptKey)
        String encryptedPW = textEncrypt.decrypt(LDAP_Config.managerPassword)
        LDAP_Config.managerPassword = textEncrypt.decrypt(LDAP_Config.managerPassword)
        LDAP_Config.save()
        render(view: "index", model: [LDAP: LDAP_Config])
    }

    def save() {
        def old_LDAP_Config = LDAP_Data.findByName("LDAP_Config")
        old_LDAP_Config.delete()
        def new_LDAP_Config = new LDAP_Data(params)
        new_LDAP_Config.name = "LDAP_Config"
        def temp_LDAP_Config = LDAP_Data.findByName("temp")
        temp_LDAP_Config.delete()
        new_LDAP_Config.save(failOnError: true)
        StrongTextEncryptor textEncrypt = new StrongTextEncryptor()
        textEncrypt.setPassword(MKey.list().get(0).encryptKey)
        String encryptedPW = textEncrypt.encrypt(params.managerPassword)
        new_LDAP_Config.managerPassword = encryptedPW

        new_LDAP_Config.save(failOnError: true)
        flash.message = "LDAP configuration updated"
        redirect(action: "index", model: [LDAP: new_LDAP_Config])


    }
}

