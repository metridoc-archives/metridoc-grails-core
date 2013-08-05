package metridoc.core

import org.jasypt.util.text.BasicTextEncryptor
import org.jasypt.util.text.StrongTextEncryptor

class EncryptionService {
    static transactional = false

    def encryptString(String target, Boolean strong) {
        if (strong) {
            StrongTextEncryptor textEncrypt = new StrongTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String encrypted = textEncrypt.encrypt(target)
            return encrypted
        } else {
            BasicTextEncryptor textEncrypt = new BasicTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String encrypted = textEncrypt.encrypt(target)
            return encrypted
        }

    }

    def decryptString(String target, Boolean strong) {
        if (strong) {
            StrongTextEncryptor textEncrypt = new StrongTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String decrypted = textEncrypt.decrypt(target)
            return decrypted
        } else {
            BasicTextEncryptor textEncrypt = new BasicTextEncryptor()
            textEncrypt.setPassword(CryptKey.list().get(0).encryptKey)
            String decrypted = textEncrypt.decrypt(target)
            return decrypted
        }

    }
}
