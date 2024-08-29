package app.cardnest.screens.pin.verify

import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authState
import app.cardnest.screens.pin.PinBaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyPinViewModel(private val auth: AuthManager, private val actions: Actions) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      val isPinCorrect = auth.verifyPin(pin.value)

      if (!isPinCorrect) {
        onError()
        return@launch
      }

      actions.afterPinVerified()

      pin.value = ""
    }
  }
}
