package metridoc.core

import org.jasypt.util.text.BasicTextEncryptor
import org.jasypt.util.text.StrongTextEncryptor

class LdapData {
    String server
    String rootDN
    String userSearchBase
    String userSearchFilter
    String managerDN
    String groupSearchBase = "NOT USED YET"
    Boolean encryptStrong = true
    String encryptedPassword

    Boolean skipAuthentication = false
    Boolean skipCredentialsCheck = false
    Boolean allowEmptyPasswords = true

    static transients = ['unencryptedPassword']

    static constraints = {
        server(blank: false)
        rootDN(blank: false)
        userSearchBase(blank: false)
        userSearchFilter(blank: false)
        managerDN(nullable: true, blank: true)
        encryptedPassword(nullable: true, blank: true)

    }

    @SuppressWarnings("UnnecessaryQualifiedReference")
    String getUnencryptedPassword() {
        //helps with testing
        if(!encryptedPassword) return null

        if (encryptStrong) {
            StrongTextEncryptor textEncrypt = new StrongTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String decrypted = textEncrypt.decrypt(encryptedPassword)
            return decrypted
        } else {
            BasicTextEncryptor textEncrypt = new BasicTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String decrypted = textEncrypt.decrypt(encryptedPassword)
            return decrypted
        }
    }
}
