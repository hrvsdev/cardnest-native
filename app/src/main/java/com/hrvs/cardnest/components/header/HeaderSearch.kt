package com.hrvs.cardnest.components.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.components.core.AppTextField
import com.hrvs.cardnest.ui.theme.TH_WHITE_60

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF00060C)
@Composable
fun HeaderSearch() {
  val search = rememberTextFieldState("")

  @Composable
  fun SearchIcon() {
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
  fun ClearButton() {
    if (search.text.isNotEmpty()) {
      IconButton(onClick = { search.clearText() }) {
        Icon(
          imageVector = Icons.Outlined.Clear, contentDescription = "Clear", tint = TH_WHITE_60
        )
      }
    }
  }

  Box(Modifier.padding(16.dp)) {
    AppTextField(
      state = search,
      placeholder = "Search",
      leftIcon = { SearchIcon() },
      rightIcon = { ClearButton() },
    )
  }
}

