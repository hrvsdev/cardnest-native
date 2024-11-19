package app.cardnest.screens.pin.unlock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonVariant
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.pin.Keypad
import app.cardnest.components.pin.PinInput
import app.cardnest.screens.password.unlock.UnlockWithPasswordScreen
import app.cardnest.screens.pin.create.create.PIN_LENGTH
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_WHITE
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UnlockWithPinScreen : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current as FragmentActivity
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<UnlockWithPinViewModel> { parametersOf(navigator) }

    val canUnlockWithBiometrics = vm.getShowBiometricsButton(ctx)

    fun onUnlockWithPassword() {
      navigator.replace(UnlockWithPasswordScreen())
    }

    fun onUnlockWithBiometrics() {
      vm.unlockWithBiometrics(ctx)
    }

    LaunchedEffect(canUnlockWithBiometrics) {
      if (canUnlockWithBiometrics) onUnlockWithBiometrics()
    }

    ScreenContainer {
      Spacer(Modifier.size(64.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Enter the PIN",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Unlock the app using your PIN", align = TextAlign.Center)
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

      Keypad(
        pin = vm.pin,
        onPinChange = vm::onPinChange,
        onPinSubmit = vm::onPinSubmit,
        showBiometricsIcon = canUnlockWithBiometrics,
        onBiometricsIconClick = ::onUnlockWithBiometrics
      )

      Spacer(Modifier.size(48.dp))
      AppButton(
        title = "Use password instead",
        onClick = ::onUnlockWithPassword,
        variant = ButtonVariant.Flat,
        isDisabled = vm.pin.value.length == PIN_LENGTH
      )
    }
  }
}
