package com.hrvs.cardnest.components.header

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.components.core.AppTextField
import com.hrvs.cardnest.ui.theme.TH_SKY_80
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_07
import com.hrvs.cardnest.ui.theme.TH_WHITE_10
import com.hrvs.cardnest.ui.theme.TH_WHITE_60

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF00060C)
@Composable
fun HeaderSearch() {
  val search = rememberTextFieldState("")

  @Composable
  fun Prefix() {
    Box(Modifier.size(48.dp), Alignment.Center) {
      Icon(
        imageVector = Icons.Outlined.Search,
        contentDescription = "Search",
        modifier = Modifier.size(28.dp),
        tint = TH_WHITE_60
      )
    }
  }

  @Composable
  fun Suffix() {
    if (search.text.isNotEmpty()) {
      IconButton(onClick = { search.edit { replace(0, search.text.length, "") } }) {
        Icon(
          imageVector = Icons.Outlined.Clear, contentDescription = "Clear", tint = TH_WHITE_60
        )
      }
    }
  }

  Box(Modifier.padding(horizontal = 16.dp)) {
    AppTextField(
      state = search,
      placeholder = "Search",
      leftIcon = { Prefix() },
      rightIcon = { Suffix() },
    )
  }
}

