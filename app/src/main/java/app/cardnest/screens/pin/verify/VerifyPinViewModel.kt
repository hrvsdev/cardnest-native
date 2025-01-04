package app.cardnest.screens.pin.verify

import app.cardnest.data.actions.Actions.afterPinVerified
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.passwordData
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.existsStateInViewModel
import kotlinx.coroutines.delay

class VerifyPinViewModel(private val auth: AuthManager) : PinBaseViewModel() {
  val hasCreatedPassword = passwordData.existsStateInViewModel()

  override fun onSubmit() {
    onPinSubmit("VerifyPinViewModel") {
      auth.verifyPin(pin)
      afterPinVerified()

      delay(200)
      resetPin()
    }
  }
}
