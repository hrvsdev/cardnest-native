package app.cardnest.screens.password.verify

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.screens.user.account.AccountScreen
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class VerifyPasswordViewModel(private val authManager: AuthManager, private val navigator: Navigator) : ViewModel() {
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
        authManager.verifyPassword(state.text.toString())
        navigator.popUntil { it is AccountScreen }
      } catch (e: Exception) {
        e.toastAndLog("VerifyPasswordViewModel")
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

