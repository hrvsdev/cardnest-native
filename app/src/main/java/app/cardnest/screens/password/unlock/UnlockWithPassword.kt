package app.cardnest.screens.password.unlock

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
import app.cardnest.components.core.PasswordTextField
import app.cardnest.components.password.PasswordInfo
import app.cardnest.components.password.PasswordInfoType
import app.cardnest.screens.pin.unlock.UnlockWithPinScreen
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_WHITE
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UnlockWithPasswordScreen : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current as FragmentActivity
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<UnlockWithPasswordViewModel> { parametersOf(navigator) }

    val canUnlockWithPin = vm.getShowPinButton()
    val canUnlockWithBiometrics = vm.getShowBiometricsButton(ctx)

    fun onUnlockWithPin() {
      navigator.push(UnlockWithPinScreen())
    }

    fun onUnlockWithBiometrics() {
      vm.unlockWithBiometrics(ctx)
    }

    LaunchedEffect(Unit) {
      if (canUnlockWithBiometrics && canUnlockWithPin.not()) onUnlockWithBiometrics()
    }

    LaunchedEffect(vm.isPasswordIncorrect) {
      if (vm.isPasswordIncorrect) vm.focusRequester.requestFocus()
    }

    ScreenContainer {
      Spacer(Modifier.size(64.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Enter your password",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Unlock the app using your password", align = TextAlign.Center)
      }

      Spacer(Modifier.size(32.dp))
      Column {
        PasswordTextField(
          state = vm.state,
          placeholder = "Enter password",
          isLoading = vm.isLoading,
          focusRequester = vm.focusRequester,
          onKeyboardAction = { vm.onSubmit() }
        )

        Spacer(Modifier.size(8.dp))
        PasswordInfo("Entered password is incorrect", PasswordInfoType.ERROR, vm.isPasswordIncorrect)
      }

      Spacer(Modifier.weight(1f))

      when {
        canUnlockWithPin && canUnlockWithBiometrics -> {
          AppButton("Use PIN or biometrics instead", ::onUnlockWithPin, variant = ButtonVariant.Flat)
        }

        canUnlockWithPin -> {
          AppButton("Use PIN instead", ::onUnlockWithPin, variant = ButtonVariant.Flat)
        }

        canUnlockWithBiometrics -> {
          AppButton("Use biometrics instead", ::onUnlockWithBiometrics, variant = ButtonVariant.Flat)
        }
      }

      Spacer(Modifier.size(12.dp))
      AppButton("Continue", vm::onSubmit, isLoading = vm.isLoading)
    }
  }
}