package app.cardnest.screens.pin

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

abstract class PinBaseViewModel : ViewModel() {
  val pin = mutableStateOf("")
  val hasError = mutableStateOf(false)
  val showErrorMessage = mutableStateOf(false)

  fun onPinChange(newPin: String) {
    showErrorMessage.value = false
    pin.value = newPin
  }

  suspend fun onError() {
    hasError.value = true
    showErrorMessage.value = true

    delay(1000)
    hasError.value = false
    pin.value = ""
  }
}
