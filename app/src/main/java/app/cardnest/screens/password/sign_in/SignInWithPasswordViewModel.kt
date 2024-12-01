package app.cardnest.screens.password.sign_in

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.biometricsData
import app.cardnest.data.pinData
import app.cardnest.data.user.UserManager
import app.cardnest.screens.user.account.AccountScreen
import app.cardnest.utils.extensions.stateInViewModel
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SignInWithPasswordViewModel(private val userManager: UserManager, private val navigator: Navigator) : ViewModel() {
  val state = TextFieldState()
  val focusRequester = FocusRequester()

  var isLoading by mutableStateOf(false)
    private set

  var isPasswordIncorrect by mutableStateOf(false)
    private set

  val hasCreatedPin = pinData.map { it != null }.stateInViewModel(false)
  val hasEnabledBiometrics = biometricsData.map { it != null }.stateInViewModel(false)

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
        userManager.continueSignInByEnteringPassword(state.text.toString())
        navigator.popUntil { it is AccountScreen }
      } catch (e: Exception) {
        e.toastAndLog("SignInWithPasswordViewModel")
        onError()
      }
    }
  }

  private fun onError() {
    focusRequester.requestFocus()

    isPasswordIncorrect = true
    isLoading = false
  }
}

