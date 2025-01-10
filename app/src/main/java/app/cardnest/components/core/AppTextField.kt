package app.cardnest.components.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicSecureTextField
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.KeyboardActionHandler
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TextObfuscationMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cardnest.R
import app.cardnest.components.loader.LoadingIcon
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.LatoFamily
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_07
import app.cardnest.ui.theme.TH_WHITE_10
import app.cardnest.ui.theme.TH_WHITE_60
import kotlinx.coroutines.delay

@Composable
fun AppTextField(
  state: TextFieldState,

  label: String? = null,
  placeholder: String? = null,
  error: AppTextFieldError = AppTextFieldError(),
  readOnly: Boolean = false,

  inputTransformation: InputTransformation? = null,
  interactionSource: MutableInteractionSource? = null,

  focusRequester: FocusRequester = FocusRequester(),

  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  onKeyboardAction: KeyboardActionHandler? = null,

  leftIcon: @Composable (() -> Unit)? = null,
  rightIcon: @Composable (() -> Unit)? = null,

  onFocus: () -> Unit = {},
  onBlur: () -> Unit = {},
) {
  val isFocused = remember { mutableStateOf(false) }

  val paddingValues = PaddingValues(
    start = if (leftIcon == null) 16.dp else 0.dp,
    end = if (rightIcon == null) 16.dp else 0.dp
  )

  Column(Modifier.fillMaxWidth()) {
    if (label != null) AppText(label, Modifier.padding(start = 8.dp, bottom = 8.dp))

    BasicTextField(
      state = state,
      cursorBrush = SolidColor(TH_SKY),

      textStyle = TextStyle(
        color = if (!error.hasError) TH_WHITE else TH_RED,
        fontSize = 16.sp,
        fontFamily = LatoFamily
      ),

      lineLimits = TextFieldLineLimits.SingleLine,
      readOnly = readOnly,

      inputTransformation = inputTransformation,
      keyboardOptions = keyboardOptions,
      onKeyboardAction = onKeyboardAction,
      interactionSource = interactionSource,

      modifier = Modifier
        .focusRequester(focusRequester)
        .height(48.dp)
        .background(if (isFocused.value) TH_WHITE_10 else TH_WHITE_07, RoundedCornerShape(14.dp))
        .onFocusChanged {
          isFocused.value = it.isFocused
          if (it.isFocused) onFocus() else onBlur()
        },

      decorator = { innerTextField ->
        Row(Modifier.padding(paddingValues), verticalAlignment = Alignment.CenterVertically) {
          if (leftIcon != null) leftIcon()

          Box(Modifier.weight(1f), Alignment.CenterStart) {
            if (state.text.isEmpty() && placeholder != null) {
              AppText(text = placeholder, color = TH_WHITE_60)
            }
            innerTextField()
          }

          if (rightIcon != null) rightIcon()
        }
      }
    )

    AnimatedVisibility(error.hasError) {
      Spacer(modifier = Modifier.height(8.dp))
    }

    AnimatedVisibility(error.hasError) {
      AppText(error.message, Modifier.padding(start = 8.dp), AppTextSize.SM, color = TH_RED)
    }
  }
}

@Composable
fun PasswordTextField(
  state: TextFieldState,
  placeholder: String? = null,

  focusRequester: FocusRequester = FocusRequester(),
  onKeyboardAction: KeyboardActionHandler? = null,

  leftIconId: Int = R.drawable.tabler__lock_password,

  isLoading: Boolean = false,
  isDisabled: Boolean = false,

  onFocus: () -> Unit = {},
  onBlur: () -> Unit = {},
) {
  var isFocused by remember { mutableStateOf(false) }
  var isVisible by remember { mutableStateOf(false) }

  var offsetY by remember { mutableIntStateOf(0) }

  val isEnabled = isLoading.not() && isDisabled.not()
  val iconColor = TH_WHITE_60

  LaunchedEffect(isVisible) {
    offsetY = 2
    delay(200)
    offsetY = 0
  }

  LaunchedEffect(isEnabled) {
    if (isEnabled.not()) {
      isVisible = false
    }
  }

  Column(Modifier.fillMaxWidth()) {
    BasicSecureTextField(
      state = state,
      cursorBrush = SolidColor(TH_SKY),

      enabled = isEnabled,

      textStyle = TextStyle(TH_WHITE, 16.sp, fontFamily = LatoFamily),
      onKeyboardAction = onKeyboardAction,
      textObfuscationMode = if (isVisible) TextObfuscationMode.Visible else TextObfuscationMode.RevealLastTyped,

      modifier = Modifier
        .focusRequester(focusRequester)
        .height(48.dp)
        .background(if (isFocused) TH_WHITE_10 else TH_WHITE_07, RoundedCornerShape(14.dp))
        .onFocusChanged {
          isFocused = it.isFocused
          if (it.isFocused) onFocus() else onBlur()
        },

      decorator = { innerTextField ->
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(Modifier.size(48.dp), Alignment.Center) {
            Icon(painter = painterResource(leftIconId), contentDescription = "", tint = iconColor)
          }

          Box(Modifier.weight(1f), Alignment.CenterStart) {
            if (state.text.isEmpty() && placeholder != null) {
              AppText(text = placeholder, color = TH_WHITE_60)
            }
            innerTextField()
          }

          if (isLoading && isDisabled.not()) {
            Box(Modifier.size(48.dp), Alignment.Center) {
              LoadingIcon(size = 24.dp)
            }
          } else if (isEnabled) {
            IconButton({ isVisible = !isVisible }, Modifier.offset { IntOffset(0, y = offsetY) }) {
              if (isVisible) {
                Icon(painterResource(R.drawable.tabler__eye_off), "Hide password", tint = iconColor)
              } else {
                Icon(painterResource(R.drawable.tabler__eye), "Reveal password", tint = iconColor)
              }
            }
          }
        }
      }
    )
  }
}

@Composable
fun CopyableTextField(text: String, label: String? = null, textToCopy: String = text) {
  val clipboard = LocalClipboardManager.current

  val textState = TextFieldState(text, TextRange(text.length))

  var hasCopied by remember { mutableStateOf(false) }
  var offsetY by remember { mutableIntStateOf(0) }

  fun copyText() {
    hasCopied = true
    clipboard.setText(AnnotatedString(textToCopy))
  }

  LaunchedEffect(hasCopied) {
    if (hasCopied) {
      offsetY = 2
      delay(200)
      offsetY = 0
      delay(1500)
      hasCopied = false
    }
  }

  AppTextField(
    state = textState,
    label = label,
    readOnly = true,
    rightIcon = {
      IconButton(::copyText, Modifier.offset { IntOffset(0, y = offsetY) }, !hasCopied) {
        if (hasCopied) {
          Icon(painterResource(R.drawable.tabler__copy_check), "Text copied", tint = TH_WHITE)
        } else {
          Icon(painterResource(R.drawable.tabler__copy), "Copy text", tint = TH_WHITE)
        }
      }
    }
  )
}

open class AppTextFieldError(
  open val message: String = "Please enter a valid value",
  open val hasError: Boolean = false,
)
