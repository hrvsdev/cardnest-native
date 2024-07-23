package app.cardnest.screens.pin.enter

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
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.pin.Keypad
import app.cardnest.components.pin.PinInput
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.create.PIN_LENGTH
import app.cardnest.state.auth.AuthDataViewModel
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_WHITE
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class EnterPinScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val authVM = koinViewModel<AuthDataViewModel>()

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
        val isPinCorrect = authVM.verifyAndSetAppPin(pin)

        if (!isPinCorrect) {
          hasError = true
          showErrorMessage = true

          delay(1000)
          hasError = false
          pin = ""

          return@launch
        }

        delay(500)
        navigator.replaceAll(HomeScreen)
      }
    }

    ScreenContainer {
      Spacer(Modifier.weight(1f))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Enter the PIN",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Please enter your pin to proceed.", Modifier.padding(bottom = 32.dp))

        PinInput(pin, hasError, if (hasError) false else pin.length == PIN_LENGTH)

        AppText(
          text = if (showErrorMessage) "Entered PIN is incorrect" else "",
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
