package app.cardnest.data.auth

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
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
  private val allowedAuthenticators = BIOMETRIC_STRONG

  private val enableBiometricsPromptInfo = BiometricPrompt.PromptInfo.Builder()
    .setTitle("Register your biometrics")
    .setDescription("To enable biometrics authentication, use your fingerprint, face or iris")
    .setAllowedAuthenticators(allowedAuthenticators)
    .setNegativeButtonText("Cancel")
    .build()

  private val unlockWithBiometricsPromptInfo = BiometricPrompt.PromptInfo.Builder()
    .setTitle("Unlock CardNest")
    .setDescription("To decrypt data, use your fingerprint, face or iris to unlock CardNest")
    .setAllowedAuthenticators(allowedAuthenticators)
    .setNegativeButtonText("Use PIN")
    .build()

  suspend fun setPin(pin: String) {
    val salt = crypto.generateSalt()
    val kek = crypto.deriveKey(pin.toCharArray(), salt)

    val dek = authState.value.dek ?: crypto.generateKey()
    val encryptedDek = crypto.encryptData(crypto.keyToString(dek), kek)

    repo.setAuthData(authData.value.copy(salt = salt, encryptedDek = encryptedDek, hasCreatedPin = true))
    authState.update { it.copy(pin = pin, dek = dek) }
  }

  suspend fun removePin() {
    repo.setAuthData(AuthData())
    authState.update { it.copy(pin = null, dek = null) }
  }

  fun verifyPin(pin: String): Boolean {
    return getDekString(pin) != null
  }

  fun unlockWithPin(pin: String): Boolean {
    val dekString = getDekString(pin) ?: return false
    authState.update { it.copy(pin = pin, dek = crypto.stringToKey(dekString)) }

    return true
  }

  private fun getDekString(pin: String): String? {
    val salt = authData.value.salt ?: return null
    val encryptedDek = authData.value.encryptedDek ?: return null

    val kek = crypto.deriveKey(pin.toCharArray(), salt)
    val dekString = crypto.decryptData(encryptedDek, kek)

    return dekString
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

  fun getAreBiometricsAvailable(ctx: FragmentActivity): Boolean {
    var isBiometricsAvailable = false

    when (BiometricManager.from(ctx).canAuthenticate(allowedAuthenticators)) {
      BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
        Log.i("BiometricStatus", "No suitable biometric hardware.")
      }

      BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
        Log.i("BiometricStatus", "The hardware is unavailable. Try again later.")
      }

      BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
        Log.i("BiometricStatus", "No biometrics are enrolled.")
      }

      BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
        Log.i("BiometricStatus", "Sensors are unavailable until a security update for vulnerable sensors.")
      }

      BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
        Log.i("BiometricStatus", "Secure biometrics not supported on current Android version.")
      }

      BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
        Log.i("BiometricStatus", "Can't authenticate. Unknown biometric status.")
      }

      BiometricManager.BIOMETRIC_SUCCESS -> {
        Log.i("BiometricStatus", "Can authenticate using biometrics.")
        isBiometricsAvailable = true
      }
    }

    return isBiometricsAvailable
  }

  private suspend fun authenticate(
    ctx: FragmentActivity,
    cipher: Cipher,
    promptInfo: BiometricPrompt.PromptInfo,
    onSuccess: () -> Unit
  ) {
    if (!getAreBiometricsAvailable(ctx)) return

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
