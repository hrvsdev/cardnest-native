package app.cardnest.screens.pin.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.pin.Keypad
import app.cardnest.components.pin.PinInput
import app.cardnest.screens.user.security.SecurityScreen
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_WHITE
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.popUntil
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class ConfirmPinScreen(val enteredPin: String) : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val scope = rememberCoroutineScope()

    val pinState = remember { mutableStateOf("") }

    var pin by pinState
    var hasError by remember { mutableStateOf(false) }
    var showErrorMessage by remember { mutableStateOf(false) }

    fun onPinChange(newPin: String) {
      showErrorMessage = false
      pin = newPin
    }

    fun onPinSubmit() {
      scope.launch {
        if (pin != enteredPin) {
          hasError = true
          showErrorMessage = true

          delay(1000)
          hasError = false
          pin = ""

          return@launch
        }

        delay(500)
        repeat(3) { navigator.pop() }
      }
    }

    SubScreenRoot(title = "") {
      Spacer(Modifier.weight(1f))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Confirm the PIN",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Please confirm the PIN you entered.")
        AppText("Remember it as there is no way to recover it.", Modifier.padding(bottom = 32.dp))

        PinInput(pin, hasError, if (hasError) false else pin.length == PIN_LENGTH)

        AppText(
          text = if (showErrorMessage) "Both PIN don't match" else "",
          modifier = Modifier.padding(top = 24.dp),
          size = AppTextSize.SM,
          color = TH_RED
        )
      }

      Spacer(Modifier.weight(2f))
      Keypad(pinState, ::onPinChange, ::onPinSubmit)
      Spacer(Modifier.weight(1f))
    }
  }
}