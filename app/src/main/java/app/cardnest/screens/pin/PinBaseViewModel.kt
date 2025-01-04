package app.cardnest.screens.pin

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.screens.pin.create.create.PIN_LENGTH
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

abstract class PinBaseViewModel : ViewModel() {
  var pin by mutableStateOf("")
    private set

  var hasError by mutableStateOf(false)
    private set

  var showErrorMessage by mutableStateOf(false)
    private set

  val hasMaxLength by derivedStateOf { pin.length == PIN_LENGTH }

  abstract fun onSubmit()

  fun onPinSubmit(tag: String, afterValidation: suspend PinBaseViewModel.() -> Unit) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        afterValidation()
      } catch (e: Exception) {
        e.toastAndLog(tag)
        onError()
      }
    }
  }

  fun onKeyClick(key: String) {
    showErrorMessage = false

    if (pin.length < PIN_LENGTH) {
      pin += key
    }

    if (pin.length == PIN_LENGTH) {
      onSubmit()
    }
  }

  fun onBackspaceClick() {
    if (pin.isNotEmpty()) {
      pin = pin.dropLast(1)
    }
  }

  protected fun resetPin() {
    pin = ""
  }

  private suspend fun onError() {
    hasError = true
    showErrorMessage = true

    delay(1000)
    hasError = false
    pin = ""
  }
}
