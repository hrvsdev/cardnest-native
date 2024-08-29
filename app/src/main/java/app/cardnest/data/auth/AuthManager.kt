package app.cardnest.data.auth

import android.util.Log
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import app.cardnest.data.authData
import app.cardnest.data.authState
import app.cardnest.db.auth.AuthRepository
import app.cardnest.utils.crypto.CryptoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    val kek = crypto.deriveKey(pin.toCharArray(), salt)

    val dek = crypto.generateKey()
    val encryptedDek = crypto.encryptData(crypto.keyToString(dek), kek)

    val data = AuthData(
      salt = salt,
      encryptedDek = encryptedDek,
      encryptedBiometricsDek = null,
      hasCreatedPin = true,
      hasBiometricsEnabled = false
    )

    repo.setAuthData(data)
    authState.update { it.copy(pin = pin, dek = dek) }
  }

  suspend fun removePin() {
    repo.setAuthData(AuthData())
    authState.update { it.copy(pin = null, dek = null) }
  }

  fun verifyAndSetAppPin(pin: String): Boolean {
    val salt = authData.value.salt ?: return false
    val encryptedDek = authData.value.encryptedDek ?: return false

    val kek = crypto.deriveKey(pin.toCharArray(), salt)
    val dekString = crypto.decryptData(encryptedDek, kek)

    val isPinCorrect = dekString != null && dekString.isNotEmpty()

    if (isPinCorrect) {
      authState.update { it.copy(pin = pin, dek = crypto.stringToKey(dekString)) }
    }

    return isPinCorrect
  }

  suspend fun enableBiometrics(ctx: FragmentActivity, scope: CoroutineScope) {
    val dek = authState.value.dek ?: return

    val androidKey = crypto.getOrCreateAndroidSecretKey()
    val cipher = crypto.getInitializedCipherForEncryption(androidKey)

    authenticate(ctx, cipher, enableBiometricsPromptInfo) {
      scope.launch(Dispatchers.IO) {
        val encryptedDek = crypto.encryptDataWithCipher(crypto.keyToString(dek), cipher)
        val data = authData.value.copy(encryptedBiometricsDek = encryptedDek, hasBiometricsEnabled = true)

        repo.setAuthData(data)
      }
    }
  }

  suspend fun disableBiometrics() {
    val data = authData.value.copy(encryptedBiometricsDek = null, hasBiometricsEnabled = false)
    repo.setAuthData(data)
  }

  suspend fun unlockWithBiometrics(ctx: FragmentActivity, onSuccess: () -> Unit) {
    val encryptedDek = authData.value.encryptedBiometricsDek ?: return

    val androidKey = crypto.getOrCreateAndroidSecretKey()
    val cipher = crypto.getInitializedCipherForDecryption(androidKey, encryptedDek.iv)

    authenticate(ctx, cipher, unlockWithBiometricsPromptInfo) {
      val dekString = crypto.decryptDataWithCipher(encryptedDek.ciphertext, cipher)
      authState.update { it.copy(dek = crypto.stringToKey(dekString)) }

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
