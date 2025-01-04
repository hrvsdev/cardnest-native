package app.cardnest.screens.pin.create.create

import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.screens.pin.create.confirm.ConfirmPinScreen
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay

class CreatePinViewModel(private val navigator: Navigator) : PinBaseViewModel() {
  override fun onSubmit() {
    onPinSubmit("CreatePinViewModel") {
      delay(500)
      navigator.push(ConfirmPinScreen(pin))

      delay(200)
      resetPin()
    }
  }
}
