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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cardnest.components.button.AppButton
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.core.PasswordTextField
import app.cardnest.components.password.PasswordInfo
import app.cardnest.components.password.PasswordInfoType
import app.cardnest.screens.password.unlock.help.UnlockWithPasswordHelpScreen
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_WHITE
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UnlockWithNewPasswordScreen : Screen {
  @OptIn(ExperimentalVoyagerApi::class)
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<UnlockWithPasswordViewModel> { parametersOf(navigator) }

    fun onHelp() {
      navigator.push(UnlockWithPasswordHelpScreen())
    }

    LifecycleEffectOnce {
      vm.currentPasswordFocusRequester.requestFocus()
    }

    LaunchedEffect(vm.isCurrentPasswordIncorrect) {
      if (vm.isCurrentPasswordIncorrect) vm.currentPasswordFocusRequester.requestFocus()
    }

    SubScreenRoot(title = "", rightButtonLabel = "Help", onRightButtonClick = ::onHelp) {
      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Enter your new password",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Your password was changed from another device.", align = TextAlign.Center)
        AppText("Unlock using your new password.", align = TextAlign.Center)
      }

      Spacer(Modifier.size(32.dp))
      Column {
        PasswordTextField(
          state = vm.currentPasswordState,
          placeholder = "Enter new password",
          isLoading = vm.isVerifying,
          focusRequester = vm.currentPasswordFocusRequester,
          onKeyboardAction = { vm.onUnlockWithNewPasswordSubmit() }
        )

        Spacer(Modifier.size(8.dp))
        PasswordInfo("Entered password is incorrect", PasswordInfoType.ERROR, vm.isCurrentPasswordIncorrect)
      }

      Spacer(Modifier.weight(1f))

      Spacer(Modifier.size(12.dp))
      AppButton("Continue", vm::onUnlockWithNewPasswordSubmit, isLoading = vm.isVerifying)
    }
  }
}
