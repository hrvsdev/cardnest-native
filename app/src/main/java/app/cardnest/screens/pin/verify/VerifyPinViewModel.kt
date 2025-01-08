package app.cardnest.screens.pin.verify

import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions.afterPinVerified
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.passwordData
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.existsStateInViewModel
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VerifyPinViewModel(private val auth: AuthManager, private val navigator: Navigator) : PinBaseViewModel() {
  val hasCreatedPassword = passwordData.existsStateInViewModel()

  init {
    viewModelScope.launch(Dispatchers.Default) {
      snapshotFlow { navigator.lastItem }.collectLatest {
        if (it is VerifyPinScreen && afterPinVerified.action == null) {
          navigator.pop()
        }
      }
    }
  }

  override fun onSubmit() {
    onPinSubmit("VerifyPinViewModel") {
      auth.verifyPin(pin)
      afterPinVerified()

      delay(200)
      resetPin()
    }
  }
}
