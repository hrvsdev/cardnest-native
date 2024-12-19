package app.cardnest.screens.password.verify

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cardnest.components.button.AppButton
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.core.PasswordTextField
import app.cardnest.components.password.PasswordInfo
import app.cardnest.components.password.PasswordInfoType
import app.cardnest.screens.password.sign_in.help.ForgotPasswordBottomSheetScreen
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.utils.extensions.open
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class VerifyPasswordScreen(private val buttonLabel: String = "Continue") : Screen {
  @Composable
  override fun Content() {
    val focusManager = LocalFocusManager.current
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<VerifyPasswordViewModel> { parametersOf(navigator) }

    fun onForgotPassword() {
      focusManager.clearFocus()
      bottomSheetNavigator.open(ForgotPasswordBottomSheetScreen()) {
        bottomSheetNavigator.hide()
      }
    }

    LaunchedEffect(vm.isCurrentPasswordIncorrect) {
      if (vm.isCurrentPasswordIncorrect) vm.currentPasswordFocusRequester.requestFocus()
    }

    SubScreenRoot(title = "", rightButtonLabel = "Forgot password?", onRightButtonClick = ::onForgotPassword) {
      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Verify your password",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Verify your password to confirm and proceed.", align = TextAlign.Center)

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
        AppButton(buttonLabel, vm::onSubmit, isLoading = vm.isVerifying)
      }
    }
  }
}
