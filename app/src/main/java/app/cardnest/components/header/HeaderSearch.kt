package app.cardnest.components.header

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cardnest.R
import app.cardnest.components.core.AppTextField
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_WHITE_60

@Composable
fun HeaderSearch(queryState: TextFieldState, noOfResults: Int, totalResults: Int) {
  val showResults = queryState.text.isNotEmpty()

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
    if (queryState.text.isNotEmpty()) {
      IconButton(onClick = { queryState.clearText() }) {
        Icon(
          painter = painterResource(R.drawable.tabler__circle_x),
          contentDescription = "Clear text",
          tint = TH_WHITE_60
        )
      }
    }
  }

  Column(Modifier.padding(16.dp, 16.dp, 16.dp)) {
    AppTextField(
      state = queryState,
      placeholder = "Enter card number, bank or network",
      leftIcon = { SearchIcon() },
      rightIcon = { ClearButton() },
    )

    Spacer(Modifier.size(8.dp))

    AnimatedVisibility(showResults) {
      SearchResultStatus(noOfResults, totalResults) { queryState.clearText() }
    }

    Spacer(Modifier.size(8.dp))
  }
}

@Composable
private fun SearchResultStatus(noOfResults: Int, totalResults: Int, clear: () -> Unit) {
  Row(Modifier.padding(horizontal = 8.dp)) {
    AppText(
      size = AppTextSize.SM,
      text = buildAnnotatedString {
        append("Showing ")
        withStyle(SpanStyle(color = TH_SKY)) { append("$noOfResults") }
        append(" out of ")
        withStyle(SpanStyle(color = TH_SKY)) { append("$totalResults") }
        append(" ${if (totalResults == 1) "card" else "cards"}")
      },
    )

    Spacer(Modifier.weight(1f))

    AppText(
      text = "Clear",
      size = AppTextSize.SM,
      color = TH_SKY,
      modifier = Modifier
        .clickable { clear() }
        .drawBehind {
          drawLine(
            color = TH_SKY,
            strokeWidth = 1.dp.toPx(),
            start = Offset(0f, size.height - 1.sp.toPx()),
            end = Offset(size.width, size.height - 1.sp.toPx())
          )
        },
    )
  }
}

