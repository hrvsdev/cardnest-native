package app.cardnest.screens.password.unlock

import androidx.fragment.app.FragmentActivity
import app.cardnest.components.toast.AppToast
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.biometricsData
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.password.VerifyPasswordBaseViewModel
import app.cardnest.utils.extensions.existsStateInViewModel
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator

class UnlockWithPasswordViewModel(private val authManager: AuthManager, private val navigator: Navigator) : VerifyPasswordBaseViewModel() {
  val hasEnabledBiometrics = biometricsData.existsStateInViewModel()

  fun onSubmit() {
    onVerifyPasswordSubmit("UnlockWithPasswordViewModel") {
      authManager.unlockWithPassword(it)
      navigator.replaceAll(HomeScreen)
    }
  }

  fun onUnlockWithNewPasswordSubmit() {
    onVerifyPasswordSubmit("UnlockWithPasswordViewModel") {
      authManager.updateStalePassword(it)
      navigator.replaceAll(HomeScreen)
    }
  }

  fun getShouldUnlockWithBiometrics(ctx: FragmentActivity): Boolean {
    return biometricsData.value != null && authManager.getAreBiometricsAvailable(ctx)
  }

  fun unlockWithBiometrics(ctx: FragmentActivity) {
    if (authManager.getAreBiometricsAvailable(ctx).not()) {
      AppToast.error("Biometrics are not available, please unlock using your password.")
      return
    }

    launchDefault {
      try {
        authManager.unlockWithBiometrics(ctx) { navigator.replaceAll(HomeScreen) }
      } catch (e: Exception) {
        e.toastAndLog("UnlockWithPasswordViewModel")
      }
    }
  }
}

