package com.hrvs.cardnest.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.R
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.AppTextSize
import com.hrvs.cardnest.ui.theme.TH_RED
import com.hrvs.cardnest.ui.theme.TH_SKY_80
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_07
import com.hrvs.cardnest.ui.theme.TH_WHITE_10
import com.hrvs.cardnest.ui.theme.TH_WHITE_60
import kotlinx.coroutines.delay

@Composable
fun AppTextField(
  state: TextFieldState,

  label: String? = null,
  placeholder: String? = null,
  error: String? = null,
  readOnly: Boolean = false,

  inputTransformation: InputTransformation? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,

  rightIcon: @Composable (() -> Unit)? = null,

  onFocus: () -> Unit = {},
  onBlur: () -> Unit = {},
) {
  val isFocused = remember { mutableStateOf(false) }

  Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
    if (label != null) AppText(label, Modifier.padding(start = 8.dp))

    BasicTextField(
      state = state,
      textStyle = TextStyle(if (error.isNullOrEmpty()) TH_WHITE else TH_RED, fontSize = 16.sp),
      cursorBrush = SolidColor(TH_SKY_80),

      lineLimits = TextFieldLineLimits.SingleLine,
      readOnly = readOnly,

      inputTransformation = inputTransformation,
      keyboardOptions = keyboardOptions,

      modifier = Modifier
        .fillMaxWidth()
        .background(if (isFocused.value) TH_WHITE_10 else TH_WHITE_07, RoundedCornerShape(14.dp))
        .onFocusChanged {
          isFocused.value = it.isFocused
          if (it.isFocused) onFocus() else onBlur()
        },

      decorator = { innerTextField ->
        Box(Modifier.padding(start = 16.dp, end = if (rightIcon == null) 16.dp else 0.dp)) {
          Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
            Box(Modifier.height(48.dp), contentAlignment = Alignment.CenterStart) {
              if (state.text.isEmpty() && placeholder != null) {
                AppText(text = placeholder, color = TH_WHITE_60)
              }
              innerTextField()
            }

            if (rightIcon != null) rightIcon()
          }
        }
      })

    if (error != null) AppText(
      error, Modifier.padding(start = 8.dp), AppTextSize.SM, color = TH_RED
    )
  }
}

@Composable
fun CopyableTextField(
  text: String,
  label: String? = null,
  textToCopy: String = text,
) {
  val clipboard = LocalClipboardManager.current

  val textState = rememberTextFieldState(text)

  var hasCopied by remember { mutableStateOf(false) }
  var offsetY by remember { mutableStateOf(0.dp) }

  fun copyText() {
    hasCopied = true
    clipboard.setText(AnnotatedString(textToCopy))
  }

  LaunchedEffect(hasCopied) {
    if (hasCopied) {
      offsetY = 1.dp
      delay(200)
      offsetY = 0.dp
      delay(1500)
      hasCopied = false
    }
  }

  AppTextField(
    state = textState,
    label = label,
    readOnly = true,
    rightIcon = {
      IconButton(::copyText, Modifier.offset(y = offsetY), !hasCopied) {
        if (hasCopied) {
          Icon(Icons.Default.Check, "Text copied", tint = TH_WHITE)
        } else {
          Icon(painterResource(R.drawable.outline_content_copy_24), "Copy text", tint = TH_WHITE)
        }
      }
    }
  )
}
