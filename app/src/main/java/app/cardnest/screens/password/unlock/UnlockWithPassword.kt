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
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.core.PasswordTextField
import app.cardnest.components.password.PasswordInfo
import app.cardnest.components.password.PasswordInfoType
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
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<UnlockWithPasswordViewModel> { parametersOf(navigator) }

    LaunchedEffect(vm.isPasswordIncorrect) { if (vm.isPasswordIncorrect) vm.focusRequester.requestFocus() }

    ScreenContainer {
      Column(Modifier.fillMaxWidth().padding(top = 64.dp, bottom = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Enter your password",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Unlock app with your account password", align = TextAlign.Center)
      }

      Column {
        PasswordTextField(
          state = vm.state,
          placeholder = "Enter password",
          isLoading = vm.isLoading,
          focusRequester = vm.focusRequester,
          onKeyboardAction = { vm.onSubmit() }
        )

        Spacer(Modifier.size(8.dp))

        PasswordInfo("Your password is incorrect", PasswordInfoType.ERROR, vm.isPasswordIncorrect)
      }

      Spacer(Modifier.weight(1f))
      AppButton("Continue", vm::onSubmit, isLoading = vm.isLoading)
    }
  }
}
