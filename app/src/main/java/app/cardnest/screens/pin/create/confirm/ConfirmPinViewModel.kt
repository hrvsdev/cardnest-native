package app.cardnest.screens.pin.create.confirm

import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.card.CardDataManager
import app.cardnest.screens.pin.PinBaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmPinViewModel(
  private val authManager: AuthManager,
  private val cardDataManager: CardDataManager,
  private val actions: Actions,
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
