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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import org.koin.androidx.compose.koinViewModel

class CreatePasswordScreen : Screen {
  @Composable
  override fun Content() {
    val vm = koinViewModel<CreatePasswordViewModel>()

    val isFocused by vm.isFocused.collectAsStateWithLifecycle()
    val hasSubmitted by vm.hasSubmitted.collectAsStateWithLifecycle()

    LaunchedEffect(isFocused) { if (isFocused) vm.updateHasSubmitted(false) }
    LaunchedEffect(hasSubmitted) { if (hasSubmitted) vm.confirmPasswordFocusRequester.requestFocus() }

    SubScreenRoot(title = "") {
      Column(Modifier.fillMaxWidth().padding(vertical = 32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
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

      Column {
        PasswordTextField(
          state = vm.state,
          placeholder = "Enter password",
          focusRequester = vm.focusRequester,
          onFocus = { vm.updateIsFocused(true) },
          onBlur = { vm.updateIsFocused(false) },
          onKeyboardAction = { vm.onSubmit() }
        )

        Spacer(Modifier.size(8.dp))

        PasswordInfo("Your password includes space(s), which is allowed", PasswordInfoType.WARN, vm.containsSpace)
        PasswordInfo("Your password is strong and secure against brute-force attacks", PasswordInfoType.SUCCESS, vm.isSecure)

        AnimatedVisibility(hasSubmitted) {
          Box(Modifier.padding(top = 20.dp, bottom = 8.dp)) {
            PasswordTextField(
              state = vm.confirmPasswordState,
              placeholder = "Confirm password",
              focusRequester = vm.confirmPasswordFocusRequester,
              leftIconId = R.drawable.tabler__lock_check
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
      AppButton("Continue", vm::onSubmit)
    }
  }
}
