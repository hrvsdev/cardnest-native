package app.cardnest.utils.crypto

import android.security.keystore.KeyProperties
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec

private const val KEY_SIZE = 256
private const val KEY_ITERATION_COUNT = 600_000
private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA256"

private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE

data class EncryptedData(val ciphertext: ByteArray, val iv: ByteArray)

class CryptoManager {
  fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
    val spec = PBEKeySpec(password, salt, KEY_ITERATION_COUNT, KEY_SIZE)
    return SecretKeyFactory.getInstance(KEY_ALGORITHM).generateSecret(spec)
  }

  fun generateKey(): SecretKey {
    val keyGen = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM)
    keyGen.init(KEY_SIZE)

    return keyGen.generateKey()
  }

  fun generateSalt(): ByteArray {
    val salt = ByteArray(16)
    SecureRandom().nextBytes(salt)

    return salt
  }

  fun encryptData(plaintext: String, key: SecretKey): EncryptedData {
    val cipher = getCipher()
    cipher.init(Cipher.ENCRYPT_MODE, key)

    return EncryptedData(cipher.doFinal(plaintext.toByteArray()), cipher.iv)
  }

  fun decryptData(encryptedData: EncryptedData, key: SecretKey): String {
    val cipher = getCipher()
    cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, encryptedData.iv))

    return String(cipher.doFinal(encryptedData.ciphertext))
  }

  private fun getCipher(): Cipher {
    val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
    return Cipher.getInstance(transformation)
  }
}
