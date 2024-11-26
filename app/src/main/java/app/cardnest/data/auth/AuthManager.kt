package app.cardnest.data.auth

import android.util.Log
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricPrompt
import androidx.fragment.app.FragmentActivity
import app.cardnest.data.authDataLoadState
import app.cardnest.data.authState
import app.cardnest.data.biometricsData
import app.cardnest.data.hasAnyAuthData
import app.cardnest.data.initialUserState
import app.cardnest.data.passwordData
import app.cardnest.data.pinData
import app.cardnest.data.remotePasswordData
import app.cardnest.db.auth.AuthRepository
import app.cardnest.utils.crypto.CryptoManager
import app.cardnest.utils.extensions.checkNotNull
import app.cardnest.utils.extensions.decoded
import app.cardnest.utils.extensions.encoded
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.crypto.AEADBadTagException
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
    .setTitle("CardNest is locked")
    .setDescription("Use your fingerprint, face or iris to unlock CardNest")
    .setAllowedAuthenticators(allowedAuthenticators)
    .setNegativeButtonText("Cancel")
    .build()

  suspend fun collectAuthData() {
    repo.getLocalAuthData().catch { it.toastAndLog("AuthManager") }.collectLatest { d ->
      passwordData.update { d.password }
      pinData.update { d.pin }
      biometricsData.update { d.biometrics }

      authDataLoadState.update { it.copy(hasLocalLoaded = true) }
    }
  }

  suspend fun collectRemoteAuthData() {
    initialUserState.collectLatest {
      if (it != null) {
        repo.getRemoteAuthData().catch { it.toastAndLog("AuthManager") }.collectLatest { d ->
          remotePasswordData.update { d?.password }
          authDataLoadState.update { it.copy(hasRemoteLoaded = true) }
        }
      } else {
        remotePasswordData.update { null }
        authDataLoadState.update { it.copy(hasRemoteLoaded = false) }
      }
    }
  }

  suspend fun createAndSetPassword(password: String) {
    val salt = crypto.generateSalt()
    val dek = getOrCreateDek()

    val encryptedDek = encryptDek(password, salt, dek)
    val data = PasswordData(salt.encoded, encryptedDek.encoded, System.currentTimeMillis())

    authState.update { it.copy(dek = dek) }
    repo.setLocalPasswordData(data)
    repo.setRemotePasswordData(data)
  }

  suspend fun setLocalPassword(password: String) {
    val remotePasswordData = remotePasswordData.value.checkNotNull { "Sign-in with Google first to set password" }
    val dek = decryptDek(password, remotePasswordData.salt, remotePasswordData.encryptedDek)

    resetAuthData()

    authState.update { it.copy(dek = dek) }
    repo.setLocalPasswordData(remotePasswordData)
  }

  suspend fun removeLocalPassword() {
    repo.setLocalPasswordData(null)
  }

  fun verifyPassword(password: String) {
    val passwordData = passwordData.value.checkNotNull { "Create password first to verify" }
    decryptDek(password, passwordData.salt, passwordData.encryptedDek)
  }

  fun unlockWithPassword(password: String) {
    val passwordData = passwordData.value.checkNotNull { "Complete sign-in process to unlock app" }
    val dek = decryptDek(password, passwordData.salt, passwordData.encryptedDek)

    authState.update { it.copy(dek = dek) }
  }

  suspend fun createAndSetPin(pin: String) {
    val salt = crypto.generateSalt()
    val dek = getOrCreateDek()

    val encryptedDek = encryptDek(pin, salt, dek)
    val data = PinData(salt.encoded, encryptedDek.encoded, System.currentTimeMillis())

    authState.update { it.copy(dek = dek) }
    repo.setLocalPinData(data)
  }

  suspend fun removePin() {
    repo.setLocalPinData(null)
  }

  fun verifyPin(pin: String) {
    val pinData = pinData.value.checkNotNull { "Create PIN first to verify" }
    decryptDek(pin, pinData.salt, pinData.encryptedDek)
  }

  fun unlockWithPin(pin: String) {
    val pinData = pinData.value.checkNotNull { "Create PIN first to unlock app by PIN" }
    val dek = decryptDek(pin, pinData.salt, pinData.encryptedDek)

    authState.update { it.copy(dek = dek) }
  }

  suspend fun enableBiometrics(ctx: FragmentActivity, scope: CoroutineScope) {
    val dek = getOrCreateDek()

    val androidKey = crypto.getOrCreateAndroidSecretKey()
    val cipher = crypto.getInitializedCipherForEncryption(androidKey)

    authenticate(ctx, cipher, enableBiometricsPromptInfo) {
      scope.launch(Dispatchers.IO) {
        val encryptedDek = crypto.encryptDataWithCipher(crypto.keyToString(dek), cipher)
        val data = BiometricsData(encryptedDek.encoded, System.currentTimeMillis())

        authState.update { it.copy(dek = dek) }
        repo.setLocalBiometricsData(data)
      }
    }
  }

  suspend fun disableBiometrics() {
    repo.setLocalBiometricsData(null)
  }

  suspend fun unlockWithBiometrics(ctx: FragmentActivity, onSuccess: () -> Unit) {
    val encryptedDek = biometricsData.value?.encryptedDek.checkNotNull { "Enable biometrics first to unlock app by biometrics" }

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

  suspend fun resetAuthData() {
    repo.setLocalPasswordData(null)
    repo.setLocalPinData(null)
    repo.setLocalBiometricsData(null)
  }

  private suspend fun getOrCreateDek(): SecretKey {
    val dek = authState.value.dek

    if (dek != null) {
      return dek
    }

    val hasAnyAuthData = hasAnyAuthData.first()

    if (hasAnyAuthData) {
      throw IllegalStateException("Restart and unlock app again")
    }

    return crypto.generateKey()
  }

  private fun encryptDek(secret: String, salt: ByteArray, dek: SecretKey): EncryptedData {
    val kek = crypto.deriveKey(secret.toCharArray(), salt)
    val dekEncoded = crypto.keyToString(dek)

    return crypto.encryptData(dekEncoded, kek)
  }

  private fun decryptDek(secret: String, salt: String, encryptedDek: EncryptedDataEncoded): SecretKey {
    try {
      val kek = crypto.deriveKey(secret.toCharArray(), salt.decoded)
      val dekEncoded = crypto.decryptData(encryptedDek.decoded, kek)

      return crypto.stringToKey(dekEncoded)
    } catch (e: Exception) {
      when (e) {
        is AEADBadTagException -> throw Exception()
        else -> throw e
      }
    }
  }
}
