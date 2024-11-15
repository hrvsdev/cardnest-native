package app.cardnest.screens.password.create

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CreatePassword() {
  val state = rememberTextFieldState("")
  val confirmPasswordState = rememberTextFieldState("")

  val focusRequester = remember { FocusRequester() }
  val confirmPasswordFocusRequester = remember { FocusRequester() }

  var isFocused by remember { mutableStateOf(false) }
  var hasSubmitted by remember { mutableStateOf(false) }

  val requirements by remember {
    derivedStateOf {
      listOf(
        Pair("12 characters or more", state.text.length >= 12),
        Pair("At least 1 uppercase letter", state.text.any { it.isUpperCase() }),
        Pair("At least 1 lowercase letter", state.text.any { it.isLowerCase() }),
        Pair("At least 1 number", state.text.any { it.isDigit() }),
        Pair("At least 1 special character", state.text.any { it.isLetterOrDigit().not() })
      )
    }
  }

  val containsSpace by remember { derivedStateOf { state.text.contains(" ") } }
  val isSecure by remember { derivedStateOf { requirements.all { it.second } } }

  val showRequirements by remember { derivedStateOf { state.text.isNotEmpty() && isSecure.not() } }
  val showDoPasswordsMatchInfo by remember { derivedStateOf { hasSubmitted && confirmPasswordState.text.isNotEmpty() } }

  val doPasswordsMatch by remember { derivedStateOf { state.text == confirmPasswordState.text } }

  LaunchedEffect(isFocused) { if (isFocused) hasSubmitted = false }
  LaunchedEffect(hasSubmitted) { if (hasSubmitted) confirmPasswordFocusRequester.requestFocus() }

  fun onSubmit() {
    if (!isSecure) {
      focusRequester.requestFocus()
      return
    }

    hasSubmitted = true

    if (!doPasswordsMatch) {
      return
    }

    Log.d("CreatePassword", "Password: ${state.text}")
  }

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
        state = state,
        placeholder = "Enter password",
        focusRequester = focusRequester,
        onFocus = { isFocused = true },
        onBlur = { isFocused = false },
        onKeyboardAction = { onSubmit() },
      )

      Spacer(Modifier.size(8.dp))

      PasswordInfo("Your password includes space(s), which is allowed", PasswordInfoType.WARN, containsSpace)
      PasswordInfo("Your password is strong and secure against brute-force attacks", PasswordInfoType.SUCCESS, isSecure)

      AnimatedVisibility(hasSubmitted) {
        Box(Modifier.padding(top = 20.dp, bottom = 8.dp)) {
          PasswordTextField(
            state = confirmPasswordState,
            placeholder = "Confirm password",
            focusRequester = confirmPasswordFocusRequester,
            leftIconId = R.drawable.tabler__lock_check
          )
        }
      }

      AnimatedVisibility(showDoPasswordsMatchInfo, enter = fadeIn(), exit = fadeOut()) {
        PasswordInfo("Passwords do not match", PasswordInfoType.ERROR, doPasswordsMatch.not())
        PasswordInfo("Passwords match", PasswordInfoType.SUCCESS, doPasswordsMatch)
      }

      AnimatedVisibility(showRequirements, enter = fadeIn(), exit = fadeOut()) {
        Column {
          requirements.forEach { PasswordInfo(it.first, PasswordInfoType.ERROR, it.second.not()) }
          requirements.forEach { PasswordInfo(it.first, PasswordInfoType.DONE, it.second) }
        }
      }
    }

    Spacer(Modifier.weight(1f))
    AppButton("Continue", ::onSubmit)
  }
}

class CreatePasswordScreen : Screen {
  @Composable
  override fun Content() {
    CreatePassword()
  }
}
