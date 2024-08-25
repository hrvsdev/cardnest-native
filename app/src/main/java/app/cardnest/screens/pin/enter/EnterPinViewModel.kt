package app.cardnest.screens.pin.enter

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authState
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.PinBaseViewModel
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EnterPinViewModel(
  private val authManager: AuthManager,
  private val navigator: Navigator,
) : PinBaseViewModel() {
  val hasBiometricsEnabled = authState.map { it.hasBiometricsEnabled }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      val isPinCorrect = authManager.verifyAndSetAppPin(pin.value)

      if (!isPinCorrect) {
        onError()
        return@launch
      }

      navigator.replaceAll(HomeScreen)
    }
  }

  fun unlockWithBiometrics(ctx: FragmentActivity) {
    viewModelScope.launch(Dispatchers.IO) {
      authManager.unlockWithBiometrics(ctx) {
        navigator.replaceAll(HomeScreen)
      }
    }
  }
}
