package app.cardnest.screens.password.create

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions.afterPasswordCreated
import app.cardnest.data.auth.AuthManager
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreatePasswordViewModel(private val authManager: AuthManager) : ViewModel() {
  val state = TextFieldState()
  val confirmPasswordState = TextFieldState()

  val focusRequester = FocusRequester()
  val confirmPasswordFocusRequester = FocusRequester()

  val isFocused = MutableStateFlow(false)
  val hasSubmitted = MutableStateFlow(false)

  val requirements by derivedStateOf {
    listOf(
      Pair("12 characters or more", state.text.length >= 12),
      Pair("At least 1 uppercase letter", state.text.any { it.isUpperCase() }),
      Pair("At least 1 lowercase letter", state.text.any { it.isLowerCase() }),
      Pair("At least 1 number", state.text.any { it.isDigit() }),
      Pair("At least 1 special character", state.text.any { it.isLetterOrDigit().not() })
    )
  }

  val containsSpace by derivedStateOf { state.text.contains(" ") }
  val isSecure by derivedStateOf { requirements.all { it.second } }

  val showRequirements by derivedStateOf { state.text.isNotEmpty() && isSecure.not() }
  val showDoPasswordsMatchInfo by derivedStateOf { hasSubmitted.value && confirmPasswordState.text.isNotEmpty() }

  val doPasswordsMatch by derivedStateOf { state.text == confirmPasswordState.text }

  fun onSubmit() {
    if (!isSecure) {
      focusRequester.requestFocus()
      return
    }

    hasSubmitted.update { true }

    if (!doPasswordsMatch) {
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      try {
        authManager.setPassword(state.text.toString())
        afterPasswordCreated()
      } catch (e: Exception) {
        e.toastAndLog("ConfirmPinViewModel")
      }
    }
  }

  fun updateIsFocused(value: Boolean) {
    isFocused.update { value }
  }

  fun updateHasSubmitted(value: Boolean) {
    hasSubmitted.update { value }
  }
}

