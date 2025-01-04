package app.cardnest.screens.pin.create.confirm

import app.cardnest.components.toast.AppToast
import app.cardnest.data.actions.Actions.afterPinCreated
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.pinData
import app.cardnest.screens.pin.PinBaseViewModel

class ConfirmPinViewModel(private val authManager: AuthManager, private val enteredPin: String) : PinBaseViewModel() {
  override fun onSubmit() {
    val isUpdating = pinData.value != null
    onPinSubmit("ConfirmPinViewModel") {
      checkIfPinsMatch()
      authManager.createAndSetPin(pin)
      afterPinCreated()

      if (isUpdating) {
        AppToast.success("PIN has been updated")
      }
    }
  }

  private fun checkIfPinsMatch() {
    if (pin != enteredPin) {
      throw Exception()
    }
  }
}
