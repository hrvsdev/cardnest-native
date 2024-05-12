package com.hrvs.cardnest.components.core

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.BasicTextField2
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.TextFieldLineLimits
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.AppTextSize
import com.hrvs.cardnest.ui.theme.TH_RED
import com.hrvs.cardnest.ui.theme.TH_SKY_80
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_07
import com.hrvs.cardnest.ui.theme.TH_WHITE_10
import com.hrvs.cardnest.ui.theme.TH_WHITE_60

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppTextField(
  state: TextFieldState,

  label: String? = null,
  placeholder: String? = null,
  error: String? = null,

  inputTransformation: InputTransformation? = null,
  keyboardOptions: KeyboardOptions = KeyboardOptions. Default,

  onFocus: () -> Unit = {},
  onBlur: () -> Unit = {},
) {
  val isFocused = remember { mutableStateOf(false) }

  Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
    if (label != null) AppText(label, Modifier.padding(start = 8.dp))

    BasicTextField2(
      state,
      textStyle = TextStyle(if (error.isNullOrEmpty()) TH_WHITE else TH_RED, fontSize = 16.sp),
      cursorBrush = SolidColor(TH_SKY_80),

      lineLimits = TextFieldLineLimits.SingleLine,

      inputTransformation = inputTransformation,
      keyboardOptions = keyboardOptions,

      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .background(if (isFocused.value) TH_WHITE_10 else TH_WHITE_07, RoundedCornerShape(14.dp))
        .onFocusChanged {
          isFocused.value = it.isFocused
          if (it.isFocused) onFocus() else onBlur()
        },

      decorator = { innerTextField ->
        Column(Modifier.padding(start = 16.dp, end = 16.dp), Arrangement.Center) {
          Box(contentAlignment = Alignment.CenterStart) {
            if (state.text.isEmpty() && placeholder != null) {
              AppText(text = placeholder, color = TH_WHITE_60)
            }
            innerTextField()
          }
        }
      }
    )

    if (error != null) AppText(
      error, Modifier.padding(start = 8.dp), AppTextSize.SM, color = TH_RED
    )
  }
}
