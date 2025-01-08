package app.cardnest.components.containers

import androidx.compose.foundation.layout.Arrangement
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
import app.cardnest.components.header.HeaderActionButton
import app.cardnest.components.header.SubScreenHeader
import app.cardnest.ui.theme.TH_BLACK
import app.cardnest.ui.theme.TH_DARKER_BLUE

val appGradient = Brush.linearGradient(
  colors = listOf(TH_BLACK, TH_DARKER_BLUE),
  start = Offset(Float.POSITIVE_INFINITY, 0f),
  end = Offset(0f, Float.POSITIVE_INFINITY)
)

@Composable
fun TabScreenRoot(content: @Composable ColumnScope.() -> Unit) {
  Column(
    content = content,
    modifier = Modifier
      .statusBarsPadding()
      .padding(bottom = 56.dp)
      .fillMaxHeight()
      .verticalScroll(rememberScrollState(0))
  )
}

@Composable
fun SubScreenRoot(
  title: String,
  backLabel: String = "Back",

  actionLabel: String,
  actionIcon: Painter? = null,
  isLoading: Boolean = false,
  onAction: () -> Unit,

  spacedBy: Dp? = null,

  content: @Composable ColumnScope.() -> Unit
) {
  SubScreenRoot {
    SubScreenHeader(title, backLabel) {
      HeaderActionButton(label = actionLabel, icon = actionIcon, isLoading = isLoading, onClick = onAction)
    }

    SubScreenContainer(spacedBy, content)
  }
}

@Composable
fun SubScreenRoot(title: String, backLabel: String = "Back", spacedBy: Dp? = null, content: @Composable ColumnScope.() -> Unit) {
  SubScreenRoot {
    SubScreenHeader(title, backLabel)
    SubScreenContainer(spacedBy, content)
  }
}

@Composable
fun SubScreenRoot(content: @Composable ColumnScope.() -> Unit) {
  Column(Modifier.fillMaxHeight(), content = content)
}

@Composable
fun SubScreenContainer(spacedBy: Dp? = null, content: @Composable ColumnScope.() -> Unit) {
  ScreenContainer(spacedBy, Modifier.fillMaxHeight().verticalScroll(rememberScrollState()), content)
}

@Composable
fun ScreenContainer(spacedBy: Dp? = null, modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
  Column(modifier.padding(16.dp), if (spacedBy != null) Arrangement.spacedBy(spacedBy) else Arrangement.Top, content = content)
}
