package app.cardnest.state.auth

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object BiometricHelper {
  lateinit var storedEncryptedData: EncryptedData

  private val cryptographyManager = CryptographyManagerImpl()
  private val keyName = "CardNestKey"

  private val promptInfo = BiometricPrompt.PromptInfo.Builder()
    .setTitle("Unlock CardNest")
    .setSubtitle("To decrypt data, use your fingerprint, face or iris to unlock CardNest")
    .setNegativeButtonText("Use PIN")
    .build()

  fun authenticateToEncrypt(ctx: FragmentActivity) {
    val cipher = cryptographyManager.getInitializedCipherForEncryption(keyName)

    authenticate(ctx, cipher) {
      val randomNumber = (0..100).random().toString()
      val encryptedData = cryptographyManager.encryptData(randomNumber, cipher)

      storedEncryptedData = encryptedData

      Log.d("BiometricPrompt", "Random number before encryption: $randomNumber")
      Log.d("BiometricPrompt", "Encrypted number: ${encryptedData.ciphertext}")
      Log.d("BiometricPrompt", "Initialization vector: ${encryptedData.iv}")
    }
  }

  fun authenticateToDecrypt(ctx: FragmentActivity) {
    val cipher =
      cryptographyManager.getInitializedCipherForDecryption(keyName, storedEncryptedData.iv)

    authenticate(ctx, cipher) {
      val decryptedData = cryptographyManager.decryptData(storedEncryptedData.ciphertext, cipher)
      Log.d("BiometricPrompt", "Decrypted number: $decryptedData")
    }
  }


  private fun authenticate(
    ctx: FragmentActivity,
    cipher: Cipher,
    onSuccess: (BiometricPrompt.AuthenticationResult) -> Unit
  ) {
    val prompt = BiometricPrompt(ctx, object : BiometricPrompt.AuthenticationCallback() {
      override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        onSuccess(result)
      }

      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        Log.e("BiometricPrompt", "Authentication error: $errString")
      }

      override fun onAuthenticationFailed() {
        Log.e("BiometricPrompt", "Authentication failed")
      }
    })

    prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
  }
}

interface CryptographyManager {

  /**
   * This method first gets or generates an instance of SecretKey and then initializes the Cipher
   * with the key. The secret key uses [ENCRYPT_MODE][Cipher.ENCRYPT_MODE] is used.
   */
  fun getInitializedCipherForEncryption(keyName: String): Cipher

  /**
   * This method first gets or generates an instance of SecretKey and then initializes the Cipher
   * with the key. The secret key uses [DECRYPT_MODE][Cipher.DECRYPT_MODE] is used.
   */
  fun getInitializedCipherForDecryption(keyName: String, initializationVector: ByteArray): Cipher

  /**
   * The Cipher created with [getInitializedCipherForEncryption] is used here
   */
  fun encryptData(plaintext: String, cipher: Cipher): EncryptedData

  /**
   * The Cipher created with [getInitializedCipherForDecryption] is used here
   */
  fun decryptData(ciphertext: ByteArray, cipher: Cipher): String
}

data class EncryptedData(val ciphertext: ByteArray, val iv: ByteArray)

@Suppress("PrivatePropertyName")
private class CryptographyManagerImpl : CryptographyManager {
  private val KEY_SIZE: Int = 256
  private val ANDROID_KEYSTORE = "AndroidKeyStore"
  private val ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM
  private val ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE
  private val ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES

  override fun getInitializedCipherForEncryption(keyName: String): Cipher {
    val cipher = getCipher()
    val secretKey = getOrCreateSecretKey(keyName)
    cipher.init(Cipher.ENCRYPT_MODE, secretKey)
    return cipher
  }

  override fun getInitializedCipherForDecryption(keyName: String, iv: ByteArray): Cipher {
    val cipher = getCipher()
    val secretKey = getOrCreateSecretKey(keyName)
    cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
    return cipher
  }

  override fun encryptData(plaintext: String, cipher: Cipher): EncryptedData {
    val ciphertext = cipher.doFinal(plaintext.toByteArray(Charset.forName("UTF-8")))
    return EncryptedData(ciphertext, cipher.iv)
  }

  override fun decryptData(ciphertext: ByteArray, cipher: Cipher): String {
    val plaintext = cipher.doFinal(ciphertext)
    return String(plaintext, Charset.forName("UTF-8"))
  }

  private fun getCipher(): Cipher {
    val transformation = "$ENCRYPTION_ALGORITHM/$ENCRYPTION_BLOCK_MODE/$ENCRYPTION_PADDING"
    return Cipher.getInstance(transformation)
  }

  private fun getOrCreateSecretKey(keyName: String): SecretKey {
    // If Secret key was previously created for that keyName, then grab and return it.
    val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE)
    keyStore.load(null) // Keystore must be loaded before it can be accessed
    keyStore.getKey(keyName, null)?.let { return it as SecretKey }

    // if you reach here, then a new SecretKey must be generated for that keyName
    val paramsBuilder = KeyGenParameterSpec.Builder(
      keyName,
      KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
    paramsBuilder.apply {
      setBlockModes(ENCRYPTION_BLOCK_MODE)
      setEncryptionPaddings(ENCRYPTION_PADDING)
      setKeySize(KEY_SIZE)
      setUserAuthenticationRequired(true)
    }

    val keyGenParams = paramsBuilder.build()
    val keyGenerator = KeyGenerator.getInstance(
      KeyProperties.KEY_ALGORITHM_AES,
      ANDROID_KEYSTORE
    )
    keyGenerator.init(keyGenParams)
    return keyGenerator.generateKey()
  }
}

