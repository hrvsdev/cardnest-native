package app.cardnest.screens.pin.create.create

import androidx.lifecycle.viewModelScope
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.screens.pin.create.confirm.ConfirmPinScreen
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreatePinViewModel(private val navigator: Navigator) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        checkIfPinIsWeak()
        delay(500)
        navigator.push(ConfirmPinScreen(pin.value))
      } catch (e: Exception) {
        e.toastAndLog("CreatePinViewModel")
        onError()
      } finally {
        pin.value = ""
      }
    }
  }

  fun checkIfPinIsWeak() {
    if (pin.value == "123456") {
      throw Exception()
    }
  }
}
