package app.cardnest.data.auth

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import app.cardnest.data.authData
import app.cardnest.data.authDataLoadState
import app.cardnest.data.authState
import app.cardnest.data.preferencesState
import app.cardnest.data.remoteAuthData
import app.cardnest.data.userState
import app.cardnest.db.auth.AuthRepository
import app.cardnest.utils.crypto.CryptoManager
import app.cardnest.utils.extensions.checkNotNull
import app.cardnest.utils.extensions.decoded
import app.cardnest.utils.extensions.encoded
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalStateException
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

  suspend fun collectAuthData() {
    repo.getLocalAuthData().catch { it.toastAndLog("AuthManager") }.collectLatest { d ->
      authData.update { d }
      authDataLoadState.update { it.copy(hasLocalLoaded = true) }
    }
  }

  suspend fun collectRemoteAuthData() {
    userState.collectLatest {
      if (it != null) {
        repo.getRemoteAuthData().catch { it.toastAndLog("AuthManager") }.collectLatest { d ->
          remoteAuthData.update { d }
          authDataLoadState.update { it.copy(hasRemoteLoaded = true) }
        }
      } else {
        remoteAuthData.update { null }
        authDataLoadState.update { it.copy(hasRemoteLoaded = false) }
      }
    }
  }

  suspend fun setPassword(password: String) {
    val salt = crypto.generateSalt()
    val kek = crypto.deriveKey(password.toCharArray(), salt)

    val dek = authState.value.dek ?: crypto.generateKey()
    val encryptedDek = crypto.encryptData(crypto.keyToString(dek), kek)

    val data = AuthData(
      salt = salt.encoded,
      encryptedDek = encryptedDek.encoded,
      encryptedBiometricsDek = authData.value?.encryptedBiometricsDek,
      modifiedAt = System.currentTimeMillis()
    )

    authState.update { it.copy(dek = dek) }
    repo.setAuthData(data)
  }

  fun verifyPassword(password: String): Boolean {
    val authData = authData.value.checkNotNull { "Auth data must not be null when verifying password" }
    return decryptDek(password, authData) != null
  }

  fun unlockWithPassword(password: String): Boolean {
    val authData = authData.value.checkNotNull { "Auth data must not be null when unlocking app" }
    val dek = decryptDek(password, authData)

    if (dek != null) {
      authState.update { it.copy(dek = dek) }
      return true
    } else {
      return false
    }
  }

  suspend fun setPin(pin: String) {
    val salt = crypto.generateSalt()
    val kek = crypto.deriveKey(pin.toCharArray(), salt)

    val dek = authState.value.dek ?: crypto.generateKey()
    val encryptedDek = crypto.encryptData(crypto.keyToString(dek), kek)

    val data = AuthData(
      salt = salt.encoded,
      encryptedDek = encryptedDek.encoded,
      encryptedBiometricsDek = authData.value?.encryptedBiometricsDek,
      modifiedAt = System.currentTimeMillis()
    )

    authState.update { it.copy(pin = pin, dek = dek) }
    repo.setAuthData(data)
  }

  suspend fun removePin() {
    authState.update { it.copy(pin = null, dek = null) }
    repo.setLocalAuthData(null)
  }

  fun verifyPin(pin: String): Boolean {
    val authData = authData.value.checkNotNull { "Auth data must not be null when verifying PIN" }
    return decryptDek(pin, authData) != null
  }

  fun unlockWithPin(pin: String): Boolean {
    val authData = authData.value.checkNotNull { "Auth data must not be null when unlocking app" }
    val dek = decryptDek(pin, authData)

    if (dek != null) {
      authState.update { it.copy(pin = pin, dek = dek) }
      return true
    } else {
      return false
    }
  }

  fun decryptDek(pin: String, authData: AuthData): SecretKey? {
    val kek = crypto.deriveKey(pin.toCharArray(), authData.salt.decoded)
    val dekEncoded = crypto.decryptData(authData.encryptedDek.decoded, kek)

    return if (dekEncoded != null) crypto.stringToKey(dekEncoded) else null
  }

  fun decryptRemoteDek(remotePin: String): SecretKey? {
    val authData = remoteAuthData.value.checkNotNull { "Remote auth data must not be null of signed-in user" }
    return decryptDek(remotePin, authData)
  }

  suspend fun syncAuthData() {
    val localAuthData = authData.value
    val remoteAuthData = remoteAuthData.value

    when {
      localAuthData == null && remoteAuthData != null -> repo.setLocalAuthData(remoteAuthData)
      localAuthData != null && remoteAuthData == null -> repo.setRemoteAuthData(localAuthData)
      localAuthData != null && remoteAuthData != null -> {
        if (localAuthData.modifiedAt > remoteAuthData.modifiedAt) {
          repo.setRemoteAuthData(localAuthData)
        } else {
          repo.setLocalAuthData(remoteAuthData)
        }
      }

      else -> throw IllegalStateException("Local and remote auth data can't be null at the same time")
    }
  }

  fun syncAuthState(remotePin: String, remoteDek: SecretKey) {
    val localAuthData = authData.value
    val remoteAuthData = remoteAuthData.value.checkNotNull { "Remote auth data must not be null when syncing auth state" }

    if (localAuthData == null || localAuthData.modifiedAt < remoteAuthData.modifiedAt) {
      authState.update { it.copy(pin = remotePin, dek = remoteDek) }
    }
  }

  fun hasAuthDataChangedOnAnotherDevice(): Flow<Boolean> {
    val hasChanged = combine(authData, remoteAuthData, preferencesState) { local, remote, prefs ->
      when {
        prefs.sync.isSyncing.not() || local == null || remote == null -> false
        else -> local.modifiedAt < remote.modifiedAt
      }
    }

    return hasChanged.debounce(500)
  }

  suspend fun enableBiometrics(ctx: FragmentActivity, scope: CoroutineScope) {
    val dek = authState.value.dek ?: return

    val androidKey = crypto.getOrCreateAndroidSecretKey()
    val cipher = crypto.getInitializedCipherForEncryption(androidKey)

    authenticate(ctx, cipher, enableBiometricsPromptInfo) {
      scope.launch(Dispatchers.IO) {
        val encryptedDek = crypto.encryptDataWithCipher(crypto.keyToString(dek), cipher)
        val data = authData.value?.copy(encryptedBiometricsDek = encryptedDek.encoded)

        repo.setLocalAuthData(data)
      }
    }
  }

  suspend fun disableBiometrics() {
    val data = authData.value?.copy(encryptedBiometricsDek = null)
    repo.setLocalAuthData(data)
  }

  suspend fun unlockWithBiometrics(ctx: FragmentActivity, onSuccess: () -> Unit) {
    val encryptedDek = authData.value?.encryptedBiometricsDek.checkNotNull { "Biometrics key data must not be null when unlocking app" }

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
