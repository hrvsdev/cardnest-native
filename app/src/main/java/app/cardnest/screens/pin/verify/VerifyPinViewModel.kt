package app.cardnest.screens.pin.verify

import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.actions.Actions.afterPinVerified
import app.cardnest.data.auth.AuthManager
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyPinViewModel(private val auth: AuthManager) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        auth.verifyPin(pin.value)
        afterPinVerified()
      } catch (e: Exception) {
        e.toastAndLog("VerifyPinViewModel")
        onError()
      } finally {
        pin.value = ""
      }
    }
  }
}
