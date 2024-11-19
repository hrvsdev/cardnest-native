package app.cardnest.screens.password.create

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import app.cardnest.R
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

class CreatePasswordScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<CreatePasswordViewModel> { parametersOf(navigator) }

    LaunchedEffect(vm.hasSubmitted) {
      if (vm.hasSubmitted) vm.confirmPasswordFocusRequester.requestFocus()
    }

    SubScreenRoot(title = "") {
      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Create a password",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("It will be used to encrypt your data.", align = TextAlign.Center)
        AppText(
          align = TextAlign.Center,
          text = buildAnnotatedString {
            append("It never gets stored, so it ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("can't be recovered") }
            append(".")
          }
        )
        AppText(
          align = TextAlign.Center,
          text = buildAnnotatedString {
            append("If forgotten, all ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("data will be lost") }
            append(" forever.")
          }
        )
      }

      Spacer(Modifier.size(32.dp))
      Column {
        PasswordTextField(
          state = vm.state,
          placeholder = "Enter password",
          isDisabled = vm.isLoading,
          focusRequester = vm.focusRequester,
          onFocus = { vm.updateIsFocused(true) },
          onBlur = { vm.updateIsFocused(false) },
          onKeyboardAction = { vm.onSubmit() }
        )

        Spacer(Modifier.size(8.dp))

        PasswordInfo("Entered password includes space(s), which is allowed", PasswordInfoType.WARN, vm.containsSpace)
        PasswordInfo("Entered password is strong and secure against brute-force attacks", PasswordInfoType.SUCCESS, vm.isSecure)

        AnimatedVisibility(vm.hasSubmitted) {
          Box(Modifier.padding(top = 20.dp, bottom = 8.dp)) {
            PasswordTextField(
              state = vm.confirmPasswordState,
              placeholder = "Confirm password",
              isDisabled = vm.isLoading,
              focusRequester = vm.confirmPasswordFocusRequester,
              leftIconId = R.drawable.tabler__lock_check,
              onKeyboardAction = { vm.onSubmit() }
            )
          }
        }

        AnimatedVisibility(vm.showDoPasswordsMatchInfo, enter = fadeIn(), exit = fadeOut()) {
          PasswordInfo("Passwords do not match", PasswordInfoType.ERROR, vm.doPasswordsMatch.not())
          PasswordInfo("Passwords match", PasswordInfoType.SUCCESS, vm.doPasswordsMatch)
        }

        AnimatedVisibility(vm.showRequirements, enter = fadeIn(), exit = fadeOut()) {
          Column {
            vm.requirements.forEach { PasswordInfo(it.first, PasswordInfoType.ERROR, it.second.not()) }
            vm.requirements.forEach { PasswordInfo(it.first, PasswordInfoType.DONE, it.second) }
          }
        }
      }

      Spacer(Modifier.weight(1f))
      AppButton("Continue", vm::onSubmit, isLoading = vm.isLoading)
    }
  }
}
