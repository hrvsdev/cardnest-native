package app.cardnest.screens.pin.enter

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import app.cardnest.db.AuthRepository
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.state.auth.BiometricManager
import app.cardnest.state.authState
import app.cardnest.utils.crypto.CryptoManager
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnterPinViewModel(
  private val repository: AuthRepository,
  private val cryptoManager: CryptoManager,
  private val biometricManager: BiometricManager,
  private val navigator: Navigator,
) : PinBaseViewModel() {
  val hasBiometricEnabled = authState.map { it.hasBiometricEnabled }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      val isPinCorrect = verifyAndSetAppPin(pin.value)

      if (!isPinCorrect) {
        onError()
        return@launch
      }

      navigator.replaceAll(HomeScreen)
    }
  }

  private suspend fun verifyAndSetAppPin(pin: String): Boolean {
    val data = repository.getAuthData().first()

    val salt = data.salt ?: return false
    val encryptedRandomKey = data.encryptedRandomKey ?: return false

    val derivedPinKey = cryptoManager.deriveKey(pin.toCharArray(), salt)
    val randomKeyString = cryptoManager.decryptData(encryptedRandomKey, derivedPinKey)

    val isPinCorrect = randomKeyString.isNotEmpty()

    if (isPinCorrect) {
      authState.update { it.copy(pin = pin) }
    }

    return isPinCorrect
  }

  fun unlockWithBiometric(ctx: FragmentActivity) {
    viewModelScope.launch(Dispatchers.IO) {
      val data = repository.getAuthData().first()
      val encryptedPin = data.encryptedPin ?: return@launch

      val androidKey = cryptoManager.getOrCreateAndroidSecretKey()
      val cipher = cryptoManager.getInitializedCipherForDecryption(androidKey, encryptedPin.iv)

      biometricManager.authenticate(ctx, cipher, biometricManager.unlockWithBiometricPromptInfo) {
        viewModelScope.launch(Dispatchers.IO) {
          val decryptedPin = cryptoManager.decryptDataWithCipher(encryptedPin.ciphertext, cipher)

          authState.update { it.copy(pin = decryptedPin) }
          navigator.replaceAll(HomeScreen)
        }
      }
    }
  }
}
