package app.cardnest.screens.pin.verify

import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyPinViewModel(private val auth: AuthManager, private val actions: Actions) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val isPinCorrect = auth.verifyPin(pin.value)
        if (isPinCorrect.not()) throw Exception()

        actions.afterPinVerified()
      } catch (e: Exception) {
        onError()
        e.toastAndLog("VerifyPinViewModel")
      } finally {
        pin.value = ""
      }
    }
  }
}
