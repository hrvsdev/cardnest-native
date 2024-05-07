package com.hrvs.cardnest.components.core

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTextField(
  value: String,
  onValueChange: (String) -> Unit,

  label: String? = null,
  placeholder: String? = null,
  error: String? = null,

  maxLength: Int = Int.MAX_VALUE,

  keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
  visualTransformation: VisualTransformation = VisualTransformation.None,

  onFocus: () -> Unit = {},
  onBlur: () -> Unit = {},
) {
  val (focused, onFocusedChange) = remember { mutableStateOf(false) }

  @Composable
  fun Placeholder() {
    if (placeholder != null) AppText(text = placeholder, color = TH_WHITE_60)
  }

  Column(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
    if (label != null) AppText(label, Modifier.padding(start = 8.dp))

    BasicTextField(
      value = value,
      onValueChange = { if (it.length <= maxLength) onValueChange(it) },
      singleLine = true,
      textStyle = TextStyle(if (error.isNullOrEmpty()) TH_WHITE else TH_RED, fontSize = 16.sp),
      cursorBrush = SolidColor(TH_SKY_80),

      keyboardOptions = keyboardOptions,
      visualTransformation = visualTransformation,

      modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
        .background(if (focused) TH_WHITE_10 else TH_WHITE_07, RoundedCornerShape(14.dp))
        .onFocusChanged {
          onFocusedChange(it.isFocused)
          if (it.isFocused) onFocus() else onBlur()
        },

      decorationBox = {
        TextFieldDefaults.DecorationBox(
          // Core
          value = value,
          enabled = true,
          singleLine = true,
          innerTextField = it,

          // Style
          container = {},
          contentPadding = PaddingValues(start = 16.dp, end = 16.dp),

          // Components
          placeholder = { Placeholder() },

          // Unnecessary
          interactionSource = remember { MutableInteractionSource() },
          visualTransformation = VisualTransformation.None,
        )
      })

    if (error != null) AppText(
      error, Modifier.padding(start = 8.dp), AppTextSize.SM, color = TH_RED
    )
  }
}
