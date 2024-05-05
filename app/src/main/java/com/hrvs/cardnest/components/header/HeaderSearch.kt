package com.hrvs.cardnest.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.ui.theme.TH_SKY_80
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_07
import com.hrvs.cardnest.ui.theme.TH_WHITE_10
import com.hrvs.cardnest.ui.theme.TH_WHITE_60

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF00060C)
@Composable
fun HeaderSearch() {
  val (search, onSearchChange) = remember { mutableStateOf("") }
  val (focused, onFocusedChange) = remember { mutableStateOf(false) }

  @Composable
  fun Placeholder() {
    Text(
      text = "Enter card number, bank or network",
      color = TH_WHITE_60,
      fontSize = 16.sp,
      lineHeight = 20.sp
    )
  }

  @Composable
  fun Prefix() {
    Icon(
      imageVector = Icons.Outlined.Search,
      contentDescription = "Search",
      modifier = Modifier
        .padding(end = 4.dp)
        .size(28.dp),
      tint = TH_WHITE_60
    )
  }

  @Composable
  fun Suffix() {
    if (search.isNotEmpty()) {
      IconButton(onClick = { onSearchChange("") }) {
        Icon(
          imageVector = Icons.Outlined.Clear, contentDescription = "Clear", tint = TH_WHITE_60
        )
      }
    }
  }

  BasicTextField(
    value = search, onValueChange = onSearchChange, singleLine = true,
    textStyle = TextStyle(color = TH_WHITE, fontSize = 16.sp), cursorBrush = SolidColor(TH_SKY_80),

    modifier = Modifier
      .padding(16.dp)
      .fillMaxWidth()
      .height(48.dp)
      .background(if (focused) TH_WHITE_10 else TH_WHITE_07, RoundedCornerShape(14.dp))
      .onFocusChanged { onFocusedChange(it.isFocused) },

    decorationBox = {
      TextFieldDefaults.DecorationBox(
        // Core
        value = search,
        enabled = true,
        singleLine = true,
        innerTextField = it,

        // Style
        container = {},
        contentPadding = PaddingValues(start = 12.dp, end = (if (search.isEmpty()) 12 else 4).dp),

        // Components
        prefix = { Prefix() },
        placeholder = { Placeholder() },
        suffix = { Suffix() },

        // Unnecessary
        interactionSource = remember { MutableInteractionSource() },
        visualTransformation = VisualTransformation.None,
      )
    })
}

