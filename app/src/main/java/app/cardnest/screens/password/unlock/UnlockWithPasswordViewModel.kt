package app.cardnest.screens.password.unlock

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.components.toast.AppToast
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.biometricsData
import app.cardnest.screens.home.HomeScreen
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class UnlockWithPasswordViewModel(private val authManager: AuthManager, private val navigator: Navigator) : ViewModel() {
  val state = TextFieldState()
  val focusRequester = FocusRequester()

  var isLoading by mutableStateOf(false)
    private set

  var isPasswordIncorrect by mutableStateOf(false)
    private set

  init {
    viewModelScope.launch(Dispatchers.IO) {
      snapshotFlow { state.text }.collectLatest {
        if (isPasswordIncorrect) isPasswordIncorrect = false
      }
    }
  }

  fun onSubmit() {
    if (state.text.length < 12 || isPasswordIncorrect) {
      onError()
      return
    }

    isLoading = true
    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.unlockWithPassword(state.text.toString())
        navigator.replaceAll(HomeScreen)
      } catch (e: Exception) {
        e.toastAndLog("UnlockWithPasswordViewModel")
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

  private fun onError() {
    isLoading = false
    isPasswordIncorrect = true

    focusRequester.requestFocus()
  }
}

