package metridoc.core

import org.jasypt.util.text.BasicTextEncryptor
import org.jasypt.util.text.StrongTextEncryptor

class EncryptionService {
    static transactional = false

    def encryptString(LdapData data, String target) {
        try {
            StrongTextEncryptor textEncrypt = new StrongTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String encrypted = textEncrypt.encrypt(target)
            data.encryptedPassword = encrypted
            data.encryptStrong = true
        } catch (org.jasypt.exceptions.EncryptionOperationNotPossibleException ex) {
            BasicTextEncryptor textEncrypt = new BasicTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String encrypted = textEncrypt.encrypt(target)
            data.encryptedPassword = encrypted
            data.encryptStrong = false
        }
    }

    def decryptString(String target) {
        try {
            StrongTextEncryptor textEncrypt = new StrongTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String decrypted = textEncrypt.decrypt(target)
            return decrypted
        } catch (org.jasypt.exceptions.EncryptionOperationNotPossibleException ex) {
            BasicTextEncryptor textEncrypt = new BasicTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String decrypted = textEncrypt.decrypt(target)
            return decrypted
        }
    }
}
