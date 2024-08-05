package app.cardnest.utils.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import app.cardnest.data.serializables.EncryptedData
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec

private const val KEY_SIZE = 256
private const val KEY_ITERATION_COUNT = 210_000
private const val KEY_ALGORITHM = "PBKDF2WithHmacSHA512"

private const val ANDROID_KEYSTORE = "AndroidKeyStore"
private const val ANDROID_KEY_NAME = "CardNestKey"

private const val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES
private const val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
private const val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE

object CryptoManager {
  fun deriveKey(password: CharArray, salt: ByteArray): SecretKey {
    val spec = PBEKeySpec(password, salt, KEY_ITERATION_COUNT, KEY_SIZE)
    return SecretKeyFactory.getInstance(KEY_ALGORITHM).generateSecret(spec)
  }

  fun generateKey(): SecretKey {
    val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM)
    return keyGenerator.also { it.init(KEY_SIZE) }.generateKey()
  }

  fun getOrCreateAndroidSecretKey(): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).also { it.load(null) }
    val key = keyStore.getKey(ANDROID_KEY_NAME, null)

    if (key != null) return key as SecretKey

    val paramsBuilder = KeyGenParameterSpec.Builder(
      ANDROID_KEY_NAME,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )

    paramsBuilder.apply {
      setBlockModes(ENCRYPTION_BLOCK_MODE)
      setEncryptionPaddings(ENCRYPTION_PADDING)
      setKeySize(KEY_SIZE)
      setUserAuthenticationRequired(true)
    }

    val keyGenParams = paramsBuilder.build()
    val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)

    return keyGenerator.also { it.init(keyGenParams) }.generateKey()
  }

  fun generateSalt(): ByteArray {
    val salt = ByteArray(16)
    SecureRandom().nextBytes(salt)

    return salt
  }

  fun getInitializedCipherForEncryption(key: SecretKey): Cipher {
    return getCipher().also { it.init(Cipher.ENCRYPT_MODE, key) }
  }

  fun getInitializedCipherForDecryption(key: SecretKey, iv: ByteArray): Cipher {
    return getCipher().also { it.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv)) }
  }

  fun encryptDataWithCipher(plaintext: String, cipher: Cipher): EncryptedData {
    return EncryptedData(cipher.doFinal(plaintext.toByteArray()), cipher.iv)
  }

  fun decryptDataWithCipher(cipherText: ByteArray, cipher: Cipher): String {
    return String(cipher.doFinal(cipherText))
  }

  fun encryptData(plaintext: String, key: SecretKey): EncryptedData {
    val cipher = getInitializedCipherForEncryption(key)
    return encryptDataWithCipher(plaintext, cipher)
  }

  fun decryptData(encryptedData: EncryptedData, key: SecretKey): String {
    try {
      val cipher = getInitializedCipherForDecryption(key, encryptedData.iv)
      return decryptDataWithCipher(encryptedData.ciphertext, cipher)
    } catch (e: Exception) {
      return ""
    }
  }

  private fun getCipher(): Cipher {
    val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
    return Cipher.getInstance(transformation)
  }
}
