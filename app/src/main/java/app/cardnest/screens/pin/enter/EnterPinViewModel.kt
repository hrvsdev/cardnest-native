package app.cardnest.screens.pin.enter

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EnterPinViewModel(
  private val authManager: AuthManager,
  private val navigator: Navigator,
) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val isPinCorrect = authManager.unlockWithPin(pin.value)
        if (isPinCorrect.not()) throw Exception()

        navigator.replaceAll(HomeScreen)
      } catch (e: Exception) {
        onError()
        e.toastAndLog("EnterPinViewModel")
      }
    }
  }

  fun getShowBiometricsButton(ctx: FragmentActivity): Boolean {
    return authData.value?.encryptedBiometricsDek != null && authManager.getAreBiometricsAvailable(ctx)
  }

  fun unlockWithBiometrics(ctx: FragmentActivity) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.unlockWithBiometrics(ctx) { navigator.replaceAll(HomeScreen) }
      } catch (e: Exception) {
        e.toastAndLog("EnterPinViewModel")
      }
    }
  }
}
