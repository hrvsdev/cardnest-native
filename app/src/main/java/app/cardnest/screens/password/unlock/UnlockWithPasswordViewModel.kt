package app.cardnest.screens.password.unlock

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.focus.FocusRequester
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.screens.home.HomeScreen
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UnlockWithPasswordViewModel(private val authManager: AuthManager, private val navigator: Navigator) : ViewModel() {
  val state = TextFieldState()
  val focusRequester = FocusRequester()

  val isLoading = MutableStateFlow(false)
  val isPasswordIncorrect = MutableStateFlow(false)

  init {
    viewModelScope.launch(Dispatchers.IO) {
      snapshotFlow { state.text }.collectLatest {
        if (isPasswordIncorrect.value) isPasswordIncorrect.update { false }
      }
    }
  }

  fun onSubmit() {
    if (state.text.length < 12) {
      onError()
      return
    }

    if (isLoading.value) {
      return
    }

    if (isPasswordIncorrect.value) {
      focusRequester.requestFocus()
      return
    }

    isLoading.update { true }
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val isPasswordCorrect = authManager.unlockWithPassword(state.text.toString())
        if (isPasswordCorrect.not()) throw Exception()

        navigator.replaceAll(HomeScreen)
      } catch (e: Exception) {
        e.toastAndLog("ConfirmPinViewModel")
        onError()
      } finally {
        isLoading.update { false }
      }
    }
  }

  private fun onError() {
    isPasswordIncorrect.update { true }
  }
}

