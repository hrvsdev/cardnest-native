package app.cardnest.data.auth

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import app.cardnest.data.authData
import app.cardnest.data.authState
import app.cardnest.data.remoteAuthDataState
import app.cardnest.data.userState
import app.cardnest.db.auth.AuthRepository
import app.cardnest.utils.crypto.CryptoManager
import app.cardnest.utils.extensions.decoded
import app.cardnest.utils.extensions.encoded
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.crypto.Cipher
import javax.crypto.SecretKey

@OptIn(FlowPreview::class)
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

    val data = authData.value.copy(
      salt = salt.encoded,
      encryptedDek = encryptedDek.encoded,
      hasCreatedPin = true,
      modifiedAt = System.currentTimeMillis()
    )

    authState.update { it.copy(pin = pin, dek = dek) }
    repo.setAuthData(data)
  }

  suspend fun removePin() {
    authState.update { it.copy(pin = null, dek = null) }
    repo.setLocalAuthData(AuthData(modifiedAt = System.currentTimeMillis()))
  }

  fun verifyPin(pin: String): Boolean {
    return getDekString(pin, authData.value) != null
  }

  fun unlockWithPin(pin: String): Boolean {
    val dekString = getDekString(pin, authData.value) ?: return false
    authState.update { it.copy(pin = pin, dek = crypto.stringToKey(dekString)) }

    return true
  }

  suspend fun syncAuthData() {
    val localAuthData = authData.value
    val remoteAuthData = remoteAuthDataState.value.data

    if (remoteAuthData == null) {
      repo.setRemoteAuthData(localAuthData)
    } else {
      if (localAuthData.modifiedAt > remoteAuthData.modifiedAt) {
        repo.setRemoteAuthData(localAuthData)
      } else {
        repo.setLocalAuthData(remoteAuthData)
      }
    }
  }

  fun syncAuthState(remotePin: String, remoteDek: SecretKey) {
    val localAuthData = authData.value
    val remoteAuthData = checkNotNull(remoteAuthDataState.value.data) { "Remote auth data can't be null" }

    if (!localAuthData.hasCreatedPin || localAuthData.modifiedAt < remoteAuthData.modifiedAt) {
      authState.update { it.copy(pin = remotePin, dek = remoteDek) }
    }
  }

  fun getRemoteDek(remotePin: String): SecretKey? {
    val authData = remoteAuthDataState.value.data ?: return null
    val dekString = getDekString(remotePin, authData) ?: return null
    return crypto.stringToKey(dekString)
  }

  fun hasAuthDataChangedOnAnotherDevice(): Flow<Boolean> {
    val hasChanged = combine(authData, remoteAuthDataState, userState) { local, remoteState, user ->
      when {
        user == null || !user.isSyncing -> false
        remoteState.data == null -> false
        else -> local.modifiedAt < remoteState.data.modifiedAt
      }
    }

    return hasChanged.debounce(500)
  }

  private fun getDekString(pin: String, authData: AuthData): String? {
    val salt = authData.salt ?: return null
    val encryptedDek = authData.encryptedDek ?: return null

    val kek = crypto.deriveKey(pin.toCharArray(), salt.decoded)
    val dekString = crypto.decryptData(encryptedDek.decoded, kek)

    return dekString
  }

  suspend fun enableBiometrics(ctx: FragmentActivity, scope: CoroutineScope) {
    val dek = authState.value.dek ?: return

    val androidKey = crypto.getOrCreateAndroidSecretKey()
    val cipher = crypto.getInitializedCipherForEncryption(androidKey)

    authenticate(ctx, cipher, enableBiometricsPromptInfo) {
      scope.launch(Dispatchers.IO) {
        val encryptedDek = crypto.encryptDataWithCipher(crypto.keyToString(dek), cipher)
        val data = authData.value.copy(encryptedBiometricsDek = encryptedDek.encoded, hasBiometricsEnabled = true)

        repo.setLocalAuthData(data)
      }
    }
  }

  suspend fun disableBiometrics() {
    val data = authData.value.copy(encryptedBiometricsDek = null, hasBiometricsEnabled = false)
    repo.setLocalAuthData(data)
  }

  suspend fun unlockWithBiometrics(ctx: FragmentActivity, onSuccess: () -> Unit) {
    val encryptedDek = authData.value.encryptedBiometricsDek ?: return

    val androidKey = crypto.getOrCreateAndroidSecretKey()
    val cipher = crypto.getInitializedCipherForDecryption(androidKey, encryptedDek.iv.decoded)

    authenticate(ctx, cipher, unlockWithBiometricsPromptInfo) {
      val dekString = crypto.decryptDataWithCipher(encryptedDek.ciphertext.decoded, cipher)
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
