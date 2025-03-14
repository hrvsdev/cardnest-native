package app.cardnest.screens.password

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest

open class VerifyPasswordBaseViewModel() : ViewModel() {
  val currentPasswordState = TextFieldState()
  val currentPasswordFocusRequester = FocusRequester()

  var isVerifying by mutableStateOf(false)
    private set

  var isCurrentPasswordIncorrect by mutableStateOf(false)
    private set

  init {
    launchDefault {
      snapshotFlow { currentPasswordState.text }.collectLatest {
        if (isCurrentPasswordIncorrect) isCurrentPasswordIncorrect = false
      }
    }
  }

  protected fun onVerifyPasswordSubmit(tag: String, afterValidation: suspend CoroutineScope.(password: String) -> Unit) {
    if (currentPasswordState.text.length < 12 || isCurrentPasswordIncorrect) {
      onCurrentPasswordError()
      return
    }

    isVerifying = true
    launchDefault {
      try {
        afterValidation(currentPasswordState.text.toString())
      } catch (e: Exception) {
        e.toastAndLog(tag)
        onCurrentPasswordError()
      }
    }
  }

  private fun onCurrentPasswordError() {
    currentPasswordFocusRequester.requestFocus()
    isCurrentPasswordIncorrect = true
    isVerifying = false
  }
}

