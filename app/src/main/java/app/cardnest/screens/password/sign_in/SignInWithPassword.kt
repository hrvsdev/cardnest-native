package app.cardnest.screens.password.sign_in

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.components.button.AppButton
import app.cardnest.components.containers.SubScreenRoot
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

class SignInWithPasswordScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<SignInWithPasswordViewModel> { parametersOf(navigator) }

    val hasCreatedPin by vm.hasCreatedPin.collectAsStateWithLifecycle()
    val hasEnabledBiometrics by vm.hasEnabledBiometrics.collectAsStateWithLifecycle()

    val dataRemoveInfo = when {
      hasCreatedPin && hasEnabledBiometrics -> "Due to security reasons, your existing PIN and biometrics info will be removed."
      hasCreatedPin -> "Due to security reasons, your existing PIN info will be removed."
      hasEnabledBiometrics -> "Due to security reasons, your existing biometrics info will be removed."
      else -> null
    }

    LaunchedEffect(vm.isPasswordIncorrect) {
      if (vm.isPasswordIncorrect) vm.focusRequester.requestFocus()
    }

    SubScreenRoot("") {
      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Enter your password",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Complete sign-in process using your password.", align = TextAlign.Center)

        if (dataRemoveInfo != null) {
          AppText(dataRemoveInfo, align = TextAlign.Center)
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
        AppButton("Continue", vm::onSubmit, isLoading = vm.isLoading)
      }
    }
  }
}
