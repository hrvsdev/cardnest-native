package app.cardnest.screens.pin.enter

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.PinBaseViewModel
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnterPinViewModel(
  private val authManager: AuthManager,
  private val navigator: Navigator,
) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      val isPinCorrect = authManager.unlockWithPin(pin.value)

      if (!isPinCorrect) {
        onError()
        return@launch
      }

      navigator.replaceAll(HomeScreen)
    }
  }

  fun getShowBiometricsButton(ctx: FragmentActivity): Boolean {
    return authData.value?.encryptedBiometricsDek != null && authManager.getAreBiometricsAvailable(ctx)
  }

  fun unlockWithBiometrics(ctx: FragmentActivity) {
    viewModelScope.launch(Dispatchers.IO) {
      authManager.unlockWithBiometrics(ctx) {
        navigator.replaceAll(HomeScreen)
      }
    }
  }
}
