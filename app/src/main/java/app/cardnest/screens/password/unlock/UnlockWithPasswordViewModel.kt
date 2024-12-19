package app.cardnest.screens.password.unlock

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import app.cardnest.components.toast.AppToast
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.biometricsData
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.password.VerifyPasswordBaseViewModel
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UnlockWithPasswordViewModel(private val authManager: AuthManager, private val navigator: Navigator) : VerifyPasswordBaseViewModel() {
  fun onSubmit() {
    onVerifyPasswordSubmit("UnlockWithPasswordViewModel") {
      authManager.unlockWithPassword(it)
      navigator.replaceAll(HomeScreen)
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
      AppToast.error("Biometrics are not available, please unlock using your password.")
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.unlockWithBiometrics(ctx) { navigator.replaceAll(HomeScreen) }
      } catch (e: Exception) {
        e.toastAndLog("UnlockWithPasswordViewModel")
      }
    }
  }
}

