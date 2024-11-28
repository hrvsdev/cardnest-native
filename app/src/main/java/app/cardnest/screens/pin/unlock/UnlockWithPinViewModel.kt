package app.cardnest.screens.pin.unlock

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import app.cardnest.components.toast.AppToast
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.biometricsData
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnlockWithPinViewModel(private val authManager: AuthManager, private val navigator: Navigator) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.unlockWithPin(pin.value)
        navigator.replaceAll(HomeScreen)
      } catch (e: Exception) {
        e.toastAndLog("EnterPinViewModel")
        onError()
      }
    }
  }

  fun getHasEnabledBiometrics(): Boolean {
    return biometricsData.value != null
  }

  fun getAreBiometricsAvailable(ctx: FragmentActivity): Boolean {
    return authManager.getAreBiometricsAvailable(ctx)
  }

  fun unlockWithBiometrics(ctx: FragmentActivity) {
    if (getAreBiometricsAvailable(ctx).not()) {
      AppToast.error("Biometrics are not available. Please unlock using your PIN.")
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.unlockWithBiometrics(ctx) { navigator.replaceAll(HomeScreen) }
      } catch (e: Exception) {
        e.toastAndLog("UnlockWithPinViewModel")
      }
    }
  }
}
