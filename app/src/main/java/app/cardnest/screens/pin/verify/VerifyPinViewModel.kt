package app.cardnest.screens.pin.verify

import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.authState
import app.cardnest.screens.pin.PinBaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class VerifyPinViewModel(private val actions: Actions) : PinBaseViewModel() {
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