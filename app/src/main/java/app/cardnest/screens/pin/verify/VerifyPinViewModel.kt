package app.cardnest.screens.pin.verify

import androidx.lifecycle.viewModelScope
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.state.actions.ActionsViewModel
import app.cardnest.state.authState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyPinViewModel(private val actions: ActionsViewModel) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      val isPinCorrect = authState.value.pin == pin.value

      if (!isPinCorrect) {
        onError()
        return@launch
      }

      delay(500)
      actions.afterPinVerified()

      pin.value = ""
    }
  }
}
