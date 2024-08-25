package app.cardnest.data.auth

import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import app.cardnest.db.auth.AuthRepository
import app.cardnest.data.authData
import app.cardnest.data.authState
import app.cardnest.utils.crypto.CryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.crypto.Cipher

class AuthManager(private val repo: AuthRepository, private val crypto: CryptoManager) {
  val enableBiometricsPromptInfo = BiometricPrompt.PromptInfo.Builder()
    .setTitle("Register your biometrics")
    .setSubtitle("To enable biometrics authentication, use your fingerprint, face or iris")
    .setNegativeButtonText("Cancel")
    .build()

  val unlockWithBiometricsPromptInfo = BiometricPrompt.PromptInfo.Builder()
    .setTitle("Unlock CardNest")
    .setSubtitle("To decrypt data, use your fingerprint, face or iris to unlock CardNest")
    .setNegativeButtonText("Use PIN")
    .build()

  suspend fun setPin(pin: String) {
    val salt = crypto.generateSalt()
    val derivedPinKey = crypto.deriveKey(pin.toCharArray(), salt)

    val randomKeyString = crypto.generateKey().encoded.toString()
    val encryptedRandomKey = crypto.encryptData(randomKeyString, derivedPinKey)

    val data = AuthData(
      salt = salt,
      encryptedRandomKey = encryptedRandomKey,
      encryptedPin = null,
      hasCreatedPin = true,
      hasBiometricsEnabled = false
    )

    repo.setAuthData(data)
    authState.update { it.copy(pin = pin) }
  }

  suspend fun removePin() {
    repo.setAuthData(AuthData())
    authState.update { it.copy(pin = null) }
  }

  fun verifyAndSetAppPin(pin: String): Boolean {
    val salt = authData.value.salt ?: return false
    val encryptedRandomKey = authData.value.encryptedRandomKey ?: return false

    val derivedPinKey = crypto.deriveKey(pin.toCharArray(), salt)
    val randomKeyString = crypto.decryptData(encryptedRandomKey, derivedPinKey)

    val isPinCorrect = randomKeyString.isNotEmpty()

    if (isPinCorrect) {
      authState.update { it.copy(pin = pin) }
    }

    return isPinCorrect
  }

  suspend fun enableBiometrics(ctx: FragmentActivity) {
    val pin = authState.value.pin ?: return

    val androidKey = crypto.getOrCreateAndroidSecretKey()
    val cipher = crypto.getInitializedCipherForEncryption(androidKey)

    authenticate(ctx, cipher, enableBiometricsPromptInfo) {
      suspend {
        withContext(Dispatchers.IO) {
          val encryptedPin = crypto.encryptDataWithCipher(pin, cipher)
          val data = authData.value.copy(encryptedPin = encryptedPin, hasBiometricsEnabled = true)

          repo.setAuthData(data)
        }
      }
    }
  }

  suspend fun disableBiometrics() {
    repo.setAuthData(authData.value.copy(encryptedPin = null, hasBiometricsEnabled = false))
  }

  suspend fun unlockWithBiometrics(ctx: FragmentActivity, onSuccess: () -> Unit) {
    val encryptedPin = authData.value.encryptedPin ?: return

    val androidKey = crypto.getOrCreateAndroidSecretKey()
    val cipher = crypto.getInitializedCipherForDecryption(androidKey, encryptedPin.iv)

    authenticate(ctx, cipher, unlockWithBiometricsPromptInfo) {
      val decryptedPin = crypto.decryptDataWithCipher(encryptedPin.ciphertext, cipher)
      authState.update { it.copy(pin = decryptedPin) }

      onSuccess()
    }
  }

  private suspend fun authenticate(
    ctx: FragmentActivity,
    cipher: Cipher,
    promptInfo: BiometricPrompt.PromptInfo,
    onSuccess: () -> Unit
  ) {
    val prompt = BiometricPrompt(ctx, object : BiometricPrompt.AuthenticationCallback() {
      override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
        onSuccess()
      }

      override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
        Log.e("BiometricPrompt", "Authentication error: $errString")
      }

      override fun onAuthenticationFailed() {
        Log.e("BiometricPrompt", "Authentication failed")
      }
    })

    withContext(Dispatchers.Main) {
      prompt.authenticate(promptInfo, BiometricPrompt.CryptoObject(cipher))
    }
  }
}
