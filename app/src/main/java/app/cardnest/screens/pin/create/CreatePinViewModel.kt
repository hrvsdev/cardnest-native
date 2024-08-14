package app.cardnest.screens.pin.create

import androidx.lifecycle.viewModelScope
import app.cardnest.screens.pin.PinBaseViewModel
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CreatePinViewModel(private val navigator: Navigator) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      if (pin.value == "123456") {
        onError()
        return@launch
      }

      delay(500)
      navigator.push(ConfirmPinScreen(pin.value))

      pin.value = ""
    }
  }
}
