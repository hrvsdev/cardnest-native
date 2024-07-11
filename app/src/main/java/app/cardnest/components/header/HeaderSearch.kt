package app.cardnest.components.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.core.AppTextField
import app.cardnest.ui.theme.TH_WHITE_60

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF00060C)
@Composable
fun HeaderSearch() {
  val search = rememberTextFieldState("")

  @Composable
  fun SearchIcon() {
    Box(Modifier.size(48.dp), Alignment.Center) {
      Icon(
        painter = painterResource(R.drawable.tabler__search),
        contentDescription = "",
        tint = TH_WHITE_60
      )
    }
  }

  @Composable
  fun ClearButton() {
    if (search.text.isNotEmpty()) {
      IconButton(onClick = { search.clearText() }) {
        Icon(
          painter = painterResource(R.drawable.tabler__circle_x),
          contentDescription = "Clear text",
          tint = TH_WHITE_60
        )
      }
    }
  }

  Box(Modifier.padding(16.dp)) {
    AppTextField(
      state = search,
      placeholder = "Enter card number, bank or network",
      leftIcon = { SearchIcon() },
      rightIcon = { ClearButton() },
    )
  }
}

