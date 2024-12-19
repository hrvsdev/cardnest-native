package app.cardnest.screens.password.change

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.screens.password.NewPasswordBaseViewModel
import app.cardnest.screens.user.account.AccountScreen
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ChangePasswordViewModel(private val authManager: AuthManager, private val navigator: Navigator) : NewPasswordBaseViewModel() {
  val currentPasswordState = TextFieldState()
  val currentPasswordFocusRequester = FocusRequester()

  var isVerifying by mutableStateOf(false)
    private set

  var isCurrentPasswordIncorrect by mutableStateOf(false)
    private set

  var isVerified by mutableStateOf(false)
    private set

  val isNewPasswordEqualToCurrentPassword by derivedStateOf { newPasswordState.text.isNotEmpty() && newPasswordState.text == currentPasswordState.text }

  init {
    viewModelScope.launch(Dispatchers.IO) {
      snapshotFlow { currentPasswordState.text }.collectLatest {
        if (isCurrentPasswordIncorrect) isCurrentPasswordIncorrect = false
      }
    }
  }

  fun onSubmit() {
    if (isVerified) {
      onCreatePassword()
      return
    }

    if (currentPasswordState.text.length < 12 || isCurrentPasswordIncorrect) {
      onCurrentPasswordError()
      return
    }

    isVerifying = true
    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.verifyPassword(currentPasswordState.text.toString())
        isVerified = true
      } catch (e: Exception) {
        e.toastAndLog("ChangePasswordViewModel")
        onCurrentPasswordError()
      } finally {
        isVerifying = false
      }
    }
  }

  private fun onCreatePassword() {
    if (isNewPasswordEqualToCurrentPassword) {
      newPasswordFocusRequester.requestFocus()
      return
    }

    onNewPasswordSubmit("ChangePasswordViewModel") {
      authManager.createAndSetPassword(it)
      navigator.popUntil { it is AccountScreen }
    }
  }

  private fun onCurrentPasswordError() {
    currentPasswordFocusRequester.requestFocus()
    isCurrentPasswordIncorrect = true
    isVerifying = false
  }
}

