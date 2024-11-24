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

data class UnlockWithPasswordScreen(private val shouldPreferBiometrics: Boolean = true) : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current as FragmentActivity
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<UnlockWithPasswordViewModel> { parametersOf(navigator) }

    val canUnlockWithPin = vm.getShowPinButton()
    val canUnlockWithBiometrics = vm.getShowBiometricsButton(ctx)

    val unlockInfo = when {
      canUnlockWithPin && canUnlockWithBiometrics -> "Unlock using your biometrics, password or PIN"
      canUnlockWithPin -> "Unlock using your password or PIN"
      canUnlockWithBiometrics -> "Unlock using your biometrics or password"
      else -> "Unlock using your password"
    }

    fun onUnlockWithPin() {
      navigator.replace(UnlockWithPinScreen(shouldPreferBiometrics = false))
    }

    fun onUnlockWithBiometrics() {
      vm.unlockWithBiometrics(ctx)
    }

    LaunchedEffect(Unit) {
      if (canUnlockWithBiometrics && shouldPreferBiometrics) {
        onUnlockWithBiometrics()
      } else {
        vm.focusRequester.requestFocus()
      }
    }

    LaunchedEffect(vm.isPasswordIncorrect) {
      if (vm.isPasswordIncorrect) vm.focusRequester.requestFocus()
    }

    ScreenContainer {
      Spacer(Modifier.size(64.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Unlock CardNest",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText(unlockInfo, align = TextAlign.Center)
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
          AppButton("Use biometrics or PIN instead", ::onUnlockWithPin, variant = ButtonVariant.Flat)
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
