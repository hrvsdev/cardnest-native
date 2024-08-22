package app.cardnest.screens.pin.create

import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.AuthData
import app.cardnest.db.AuthRepository
import app.cardnest.data.card.CardDataManager
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.state.actions.ActionsViewModel
import app.cardnest.state.authState
import app.cardnest.utils.crypto.CryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ConfirmPinViewModel(
  private val repository: AuthRepository,
  private val cryptoManager: CryptoManager,
  private val cardDataManager: CardDataManager,
  private val actions: ActionsViewModel,
  private val enteredPin: String
) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      if (pin.value != enteredPin) {
        onError()
        return@launch
      }

      setPin(pin.value)
      cardDataManager.encryptAndSaveCards()
      actions.afterPinCreated()
    }
  }

  suspend fun setPin(pin: String) {
    val salt = cryptoManager.generateSalt()
    val derivedPinKey = cryptoManager.deriveKey(pin.toCharArray(), salt)

    val randomKeyString = cryptoManager.generateKey().encoded.toString()
    val encryptedRandomKey = cryptoManager.encryptData(randomKeyString, derivedPinKey)

    val authData = AuthData(
      salt = salt,
      encryptedRandomKey = encryptedRandomKey,
      encryptedPin = null,
      hasCreatedPin = true,
      hasBiometricsEnabled = false
    )

    repository.setAuthData(authData)
    authState.update { it.copy(pin = pin) }
  }
}
