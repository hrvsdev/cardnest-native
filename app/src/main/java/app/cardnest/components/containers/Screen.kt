package app.cardnest.components.containers

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.cardnest.components.header.SubScreenHeader
import app.cardnest.ui.theme.TH_BLACK
import app.cardnest.ui.theme.TH_DARKER_BLUE

val appGradient = Brush.linearGradient(
  listOf(TH_BLACK, TH_DARKER_BLUE),
  start = Offset(Float.POSITIVE_INFINITY, 0f),
  end = Offset(0f, Float.POSITIVE_INFINITY)
)

@Composable
fun TabScreenRoot(content: @Composable ColumnScope.() -> Unit) {
  Column(
    Modifier
      .statusBarsPadding()
      .padding(bottom = 56.dp)
      .fillMaxHeight()
      .verticalScroll(ScrollState(0))
  ) {
    content()
  }
}

@Composable
fun SubScreenRoot(
  title: String,

  leftIconLabel: String? = null,

  rightButtonLabel: String? = null,
  rightButtonIcon: Painter? = null,
  onRightButtonClick: () -> Unit = {},

  spacedBy: Dp? = null,

  content: @Composable ColumnScope.() -> Unit
) {
  Column(Modifier.fillMaxHeight()) {
    SubScreenHeader(title, leftIconLabel, rightButtonLabel, rightButtonIcon, onRightButtonClick)
    Box(Modifier.verticalScroll(rememberScrollState())) {
      ScreenContainer(spacedBy, content = content)
    }
  }
}


@Composable
fun ScreenContainer(
  spacedBy: Dp? = null,
  modifier: Modifier = Modifier,
  content: @Composable ColumnScope.() -> Unit
) {
  Column(
    modifier = modifier.padding(16.dp),
    verticalArrangement = if (spacedBy != null) Arrangement.spacedBy(spacedBy) else Arrangement.Top
  ) {
    content()
  }
}
