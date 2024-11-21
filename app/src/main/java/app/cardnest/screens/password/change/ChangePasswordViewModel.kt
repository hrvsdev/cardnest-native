package app.cardnest.screens.password.change

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
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

class ChangePasswordViewModel(private val authManager: AuthManager, private val navigator: Navigator) : ViewModel() {
  val currentPasswordState = TextFieldState()
  val newPasswordState = TextFieldState()
  val confirmPasswordState = TextFieldState()

  val currentPasswordFocusRequester = FocusRequester()
  val newPasswordFocusRequester = FocusRequester()
  val confirmPasswordFocusRequester = FocusRequester()

  var isLoading by mutableStateOf(false)
    private set

  var isCurrentPasswordIncorrect by mutableStateOf(false)
    private set

  var isVerified by mutableStateOf(false)
    private set

  var isNewPasswordFocused by mutableStateOf(false)
    private set

  var hasNewPasswordSubmitted by mutableStateOf(false)
    private set

  val requirements by derivedStateOf {
    listOf(
      Pair("12 characters or more", newPasswordState.text.length >= 12),
      Pair("At least 1 uppercase letter", newPasswordState.text.any { it.isUpperCase() }),
      Pair("At least 1 lowercase letter", newPasswordState.text.any { it.isLowerCase() }),
      Pair("At least 1 number", newPasswordState.text.any { it.isDigit() }),
      Pair("At least 1 special character", newPasswordState.text.any { it.isLetterOrDigit().not() })
    )
  }

  val isNewPasswordEqualToCurrentPassword by derivedStateOf { newPasswordState.text.isNotEmpty() && newPasswordState.text == currentPasswordState.text }

  val doesNewPasswordContainsSpace by derivedStateOf { newPasswordState.text.contains(" ") }
  val isNewPasswordSecure by derivedStateOf { requirements.all { it.second } && isNewPasswordEqualToCurrentPassword.not() }

  val showRequirements by derivedStateOf { newPasswordState.text.isNotEmpty() && isNewPasswordSecure.not() && isNewPasswordEqualToCurrentPassword.not() }
  val showDoPasswordsMatchInfo by derivedStateOf { hasNewPasswordSubmitted && confirmPasswordState.text.isNotEmpty() }

  val doPasswordsMatch by derivedStateOf { newPasswordState.text == confirmPasswordState.text }

  init {
    viewModelScope.launch(Dispatchers.IO) {
      snapshotFlow { currentPasswordState.text }.collectLatest {
        if (isCurrentPasswordIncorrect) isCurrentPasswordIncorrect = false
      }
    }

    viewModelScope.launch(Dispatchers.IO) {
      snapshotFlow { isNewPasswordFocused }.collectLatest {
        if (isNewPasswordFocused) hasNewPasswordSubmitted = false
      }
    }
  }

  fun onSubmit() {
    if (isVerified) {
      onNewPasswordSubmit()
      return
    }

    if (currentPasswordState.text.length < 12) {
      onCurrentPasswordError()
      return
    }

    if (isCurrentPasswordIncorrect) {
      currentPasswordFocusRequester.requestFocus()
      return
    }

    isLoading = true
    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.verifyPassword(currentPasswordState.text.toString())
        isVerified = true
      } catch (e: Exception) {
        e.toastAndLog("ChangePasswordViewModel")
        onCurrentPasswordError()
      } finally {
        isLoading = false
      }
    }
  }

  private fun onNewPasswordSubmit() {
    if (isNewPasswordSecure.not()) {
      newPasswordFocusRequester.requestFocus()
      return
    }

    hasNewPasswordSubmitted = true

    if (doPasswordsMatch.not()) {
      return
    }

    isLoading = true
    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.createAndSetPassword(newPasswordState.text.toString())
        navigator.popUntil { it is AccountScreen }
      } catch (e: Exception) {
        e.toastAndLog("ChangePasswordViewModel")
        onNewPasswordError()
      }
    }
  }

  fun updateIsNewPasswordFocused(value: Boolean) {
    isNewPasswordFocused = value
  }

  private fun onCurrentPasswordError() {
    isCurrentPasswordIncorrect = true
    isLoading = false
  }

  private fun onNewPasswordError() {
    isLoading = false
  }
}

