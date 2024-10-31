package app.cardnest.utils.crypto

import android.annotation.SuppressLint
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import app.cardnest.data.auth.EncryptedData
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

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
    try {
      val spec = PBEKeySpec(password, salt, KEY_ITERATION_COUNT, KEY_SIZE)
      return SecretKeyFactory.getInstance(KEY_ALGORITHM).generateSecret(spec)
    } catch (e: Exception) {
      throw RuntimeException("Failed to derive key", e)
    }
  }

  fun generateKey(): SecretKey {
    try {
      val keyGenerator = KeyGenerator.getInstance(ENCRYPTION_ALGORITHM)
      return keyGenerator.also { it.init(KEY_SIZE) }.generateKey()
    } catch (e: Exception) {
      throw RuntimeException("Failed to generate key", e)
    }
  }

  fun getOrCreateAndroidSecretKey(): SecretKey {
    try {
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
    } catch (e: Exception) {
      throw RuntimeException("Failed to get or generate Android keystore key", e)
    }
  }

  fun generateSalt(): ByteArray {
    val salt = ByteArray(16)
    SecureRandom().nextBytes(salt)

    return salt
  }

  fun getInitializedCipherForEncryption(key: SecretKey): Cipher {
    try {
      return getCipher().also { it.init(Cipher.ENCRYPT_MODE, key) }
    } catch (e: Exception) {
      when (e) {
        is InvalidKeyException -> throw RuntimeException("Encryption key is invalid", e)
        else -> throw RuntimeException("Failed to initialize encryption cipher", e)
      }
    }
  }

  fun getInitializedCipherForDecryption(key: SecretKey, iv: ByteArray): Cipher {
    try {
      return getCipher().also { it.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv)) }
    } catch (e: Exception) {
      when (e) {
        is InvalidKeyException -> throw RuntimeException("Decryption key is invalid", e)
        else -> throw RuntimeException("Failed to initialize decryption cipher", e)
      }
    }
  }

  fun encryptDataWithCipher(plaintext: String, cipher: Cipher): EncryptedData {
    try {
      return EncryptedData(cipher.doFinal(plaintext.toByteArray()), cipher.iv)
    } catch (e: Exception) {
      throw RuntimeException("Failed to encrypt data", e)
    }
  }

  fun decryptDataWithCipher(cipherText: ByteArray, cipher: Cipher): String {
    try {
      return String(cipher.doFinal(cipherText))
    } catch (e: Exception) {
      throw RuntimeException("Failed to decrypt data", e)
    }
  }

  fun encryptData(plaintext: String, key: SecretKey): EncryptedData {
    val cipher = getInitializedCipherForEncryption(key)
    return encryptDataWithCipher(plaintext, cipher)
  }

  fun decryptData(encryptedData: EncryptedData, key: SecretKey): String? {
    try {
      val cipher = getInitializedCipherForDecryption(key, encryptedData.iv)
      return decryptDataWithCipher(encryptedData.ciphertext, cipher)
    } catch (e: Exception) {
      Log.e("CryptoManager", "Failed to decrypt data", e)
      return null
    }
  }

  @SuppressLint("NewApi")
  fun keyToString(key: SecretKey): String {
    val encoded = key.encoded
    return Base64.getEncoder().encodeToString(encoded)
  }

  @SuppressLint("NewApi")
  fun stringToKey(encodedKey: String): SecretKeySpec {
    val decoded = Base64.getDecoder().decode(encodedKey)
    return SecretKeySpec(decoded, 0, decoded.size, ENCRYPTION_ALGORITHM)
  }

  private fun getCipher(): Cipher {
    val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
    return Cipher.getInstance(transformation)
  }
}
