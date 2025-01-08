package app.cardnest.screens.pin.unlock

import androidx.fragment.app.FragmentActivity
import app.cardnest.components.toast.AppToast
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.biometricsData
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.existsStateInViewModel
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator

class UnlockWithPinViewModel(private val authManager: AuthManager, private val navigator: Navigator) : PinBaseViewModel() {
  val hasEnabledBiometrics = biometricsData.existsStateInViewModel()

  override fun onSubmit() {
    onPinSubmit("UnlockWithPinViewModel") {
      authManager.unlockWithPin(pin)
      navigator.replaceAll(HomeScreen)
    }
  }

  fun getShouldUnlockWithBiometrics(ctx: FragmentActivity): Boolean {
    return biometricsData.value != null && authManager.getAreBiometricsAvailable(ctx)
  }

  fun unlockWithBiometrics(ctx: FragmentActivity) {
    if (authManager.getAreBiometricsAvailable(ctx).not()) {
      AppToast.error("Biometrics are not available. Please unlock using your PIN.")
      return
    }

    launchDefault {
      try {
        authManager.unlockWithBiometrics(ctx) { navigator.replaceAll(HomeScreen) }
      } catch (e: Exception) {
        e.toastAndLog("UnlockWithPinViewModel")
      }
    }
  }
}
