package app.cardnest.screens.password

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

open class NewPasswordBaseViewModel() : ViewModel() {
  val newPasswordState = TextFieldState()
  val newPasswordFocusRequester = FocusRequester()

  val confirmPasswordState = TextFieldState()
  val confirmPasswordFocusRequester = FocusRequester()

  var isCreating by mutableStateOf(false)
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

  val doesNewPasswordContainsSpace by derivedStateOf { newPasswordState.text.contains(" ") }
  val isNewPasswordSecure by derivedStateOf { requirements.all { it.second } }

  val showRequirements by derivedStateOf { newPasswordState.text.isNotEmpty() && isNewPasswordSecure.not() }
  val showDoPasswordsMatchInfo by derivedStateOf { hasNewPasswordSubmitted && confirmPasswordState.text.isNotEmpty() }

  val doPasswordsMatch by derivedStateOf { newPasswordState.text == confirmPasswordState.text }

  init {
    viewModelScope.launch(Dispatchers.IO) {
      snapshotFlow { isNewPasswordFocused }.collectLatest {
        if (isNewPasswordFocused) hasNewPasswordSubmitted = false
      }
    }
  }

  protected fun onNewPasswordSubmit(tag: String, afterValidation: suspend CoroutineScope.(password: String) -> Unit) {
    if (!isNewPasswordSecure) {
      onNewPasswordError()
      return
    }

    if (!hasNewPasswordSubmitted) {
      hasNewPasswordSubmitted = true
      return
    }

    if (!doPasswordsMatch) {
      confirmPasswordFocusRequester.requestFocus()
      return
    }

    isCreating = true
    viewModelScope.launch(Dispatchers.IO) {
      try {
        afterValidation(newPasswordState.text.toString())
      } catch (e: Exception) {
        e.toastAndLog(tag)
        onNewPasswordError()
      }
    }
  }

  fun updateIsNewPasswordFocused(value: Boolean) {
    isNewPasswordFocused = value
  }

  private fun onNewPasswordError() {
    newPasswordFocusRequester.requestFocus()
    isCreating = false
  }
}

