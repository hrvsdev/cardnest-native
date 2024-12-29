package app.cardnest.screens.pin.verify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.pin.Keypad
import app.cardnest.components.pin.PinInput
import app.cardnest.screens.pin.create.create.PIN_LENGTH
import app.cardnest.screens.pin.verify.help.ForgotPinBottomSheetScreen
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.utils.extensions.collectValue
import app.cardnest.utils.extensions.open
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import org.koin.androidx.compose.koinViewModel

class VerifyPinBeforeActionScreen : Screen {
  @Composable
  override fun Content() {
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<VerifyPinViewModel>()

    val hasCreatedPassword = vm.hasCreatedPassword.collectValue()

    fun onForgotPin() {
      bottomSheetNavigator.open(ForgotPinBottomSheetScreen(hasCreatedPassword)) {
        bottomSheetNavigator.hide()
      }
    }

    SubScreenRoot(title = "", rightButtonLabel = "Forgot PIN?", onRightButtonClick = ::onForgotPin) {
      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Verify your PIN",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Verify your PIN to confirm and proceed.", align = TextAlign.Center)
      }

      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        PinInput(
          pin = vm.pin.value,
          hasError = vm.hasError.value,
          isLoading = if (vm.hasError.value) false else vm.pin.value.length == PIN_LENGTH
        )

        AppText(
          text = if (vm.showErrorMessage.value) "Entered PIN is incorrect" else "",
          modifier = Modifier.padding(top = 24.dp),
          size = AppTextSize.SM,
          color = TH_RED
        )
      }

      Spacer(Modifier.weight(1f))
      Keypad(vm.pin, vm::onPinChange, vm::onPinSubmit)

      Spacer(Modifier.size(48.dp))
    }
  }
}
