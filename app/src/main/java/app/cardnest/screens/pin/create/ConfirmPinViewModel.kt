package app.cardnest.screens.pin.create

import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
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
  private val authManager: AuthManager,
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

      authManager.setPin(pin.value)
      cardDataManager.encryptAndSaveCards()
      actions.afterPinCreated()
    }
  }
}
