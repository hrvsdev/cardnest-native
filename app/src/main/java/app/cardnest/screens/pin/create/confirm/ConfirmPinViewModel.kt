package app.cardnest.screens.pin.create.confirm

import androidx.lifecycle.viewModelScope
import app.cardnest.components.toast.AppToast
import app.cardnest.data.actions.Actions.afterPinCreated
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.pinData
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ConfirmPinViewModel(private val authManager: AuthManager, private val enteredPin: String) : PinBaseViewModel() {
  fun onPinSubmit() {
    val isUpdating = pinData.value != null
    viewModelScope.launch(Dispatchers.IO) {
      try {
        checkIfPinsMatch()
        authManager.createAndSetPin(pin.value)
        afterPinCreated()

        if (isUpdating) {
          AppToast.success("PIN has been updated")
        }

      } catch (e: Exception) {
        e.toastAndLog("ConfirmPinViewModel")
        onError()
      }
    }
  }

  private fun checkIfPinsMatch() {
    if (pin.value != enteredPin) {
      throw Exception()
    }
  }
}
