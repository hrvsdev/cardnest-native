package app.cardnest.screens.password.create

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.user.UserManager
import app.cardnest.screens.user.account.AccountScreen
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class CreatePasswordViewModel(private val userManager: UserManager, private val navigator: Navigator) : ViewModel() {
  val state = TextFieldState()
  val confirmPasswordState = TextFieldState()

  val focusRequester = FocusRequester()
  val confirmPasswordFocusRequester = FocusRequester()

  var isLoading by mutableStateOf(false)
    private set

  var isFocused by mutableStateOf(false)
    private set

  var hasSubmitted by mutableStateOf(false)
    private set

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
  val showDoPasswordsMatchInfo by derivedStateOf { hasSubmitted && confirmPasswordState.text.isNotEmpty() }

  val doPasswordsMatch by derivedStateOf { state.text == confirmPasswordState.text }

  init {
    viewModelScope.launch(Dispatchers.IO) {
      snapshotFlow { isFocused }.collectLatest {
        if (isFocused) hasSubmitted = false
      }
    }
  }

  fun onSubmit() {
    if (!isSecure) {
      focusRequester.requestFocus()
      return
    }

    hasSubmitted = true

    if (!doPasswordsMatch) {
      return
    }

    isLoading = true
    viewModelScope.launch(Dispatchers.IO) {
      try {
        userManager.continueSignInByCreatingPassword(state.text.toString())
        navigator.popUntil { it is AccountScreen }
      } catch (e: Exception) {
        e.toastAndLog("CreatePasswordViewModel")
        onError()
      }
    }
  }

  private fun onError() {
    isLoading = false
  }

  fun updateIsFocused(value: Boolean) {
    isFocused = value
  }
}

