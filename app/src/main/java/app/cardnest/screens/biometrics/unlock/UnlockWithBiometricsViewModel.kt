package app.cardnest.screens.biometrics.unlock

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.biometricsData
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnlockWithBiometricsViewModel(private val authManager: AuthManager, private val navigator: Navigator) : PinBaseViewModel() {
  fun getShowBiometricsButton(ctx: FragmentActivity): Boolean {
    return biometricsData.value != null && authManager.getAreBiometricsAvailable(ctx)
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
