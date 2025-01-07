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
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.core.PasswordTextField
import app.cardnest.components.password.PasswordInfo
import app.cardnest.components.password.PasswordInfoType
import app.cardnest.screens.password.unlock.help.UnlockWithPasswordHelpScreen
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.utils.extensions.collectValue
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UnlockWithPasswordScreen : Screen {
  @OptIn(ExperimentalVoyagerApi::class)
  @Composable
  override fun Content() {
    val ctx = LocalContext.current as FragmentActivity
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<UnlockWithPasswordViewModel> { parametersOf(navigator) }

    val hasEnabledBiometrics = vm.hasEnabledBiometrics.collectValue()

    fun onHelp() {
      navigator.push(UnlockWithPasswordHelpScreen())
    }

    fun onUnlockWithBiometrics() {
      vm.unlockWithBiometrics(ctx)
    }

    LifecycleEffectOnce {
      if (navigator.lastEvent != StackEvent.Push && vm.getShouldUnlockWithBiometrics(ctx)) {
        onUnlockWithBiometrics()
      } else {
        vm.currentPasswordFocusRequester.requestFocus()
      }
    }

    LaunchedEffect(vm.isCurrentPasswordIncorrect) {
      if (vm.isCurrentPasswordIncorrect) vm.currentPasswordFocusRequester.requestFocus()
    }

    SubScreenRoot(title = "", actionLabel = "Help", onAction = ::onHelp) {
      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Unlock CardNest",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        if (hasEnabledBiometrics) {
          AppText("Unlock using your biometrics or password.", align = TextAlign.Center)
        } else {
          AppText("Unlock using your password.", align = TextAlign.Center)
        }
      }

      Spacer(Modifier.size(32.dp))
      Column {
        PasswordTextField(
          state = vm.currentPasswordState,
          placeholder = "Enter password",
          isLoading = vm.isVerifying,
          focusRequester = vm.currentPasswordFocusRequester,
          onKeyboardAction = { vm.onSubmit() }
        )

        Spacer(Modifier.size(8.dp))
        PasswordInfo("Entered password is incorrect", PasswordInfoType.ERROR, vm.isCurrentPasswordIncorrect)
      }

      Spacer(Modifier.weight(1f))

      if (hasEnabledBiometrics) {
        AppButton("Use biometrics instead", ::onUnlockWithBiometrics, variant = ButtonVariant.Flat)
      }

      Spacer(Modifier.size(12.dp))
      AppButton("Continue", vm::onSubmit, isLoading = vm.isVerifying)
    }
  }
}
