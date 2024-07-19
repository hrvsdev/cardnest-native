package app.cardnest.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter

@Composable
fun SettingsButton(
  title: String,
  icon: Painter,
  isDanger: Boolean = false,
  isFirst: Boolean = false,
  isLast: Boolean = false,
  onClick: () -> Unit,
) {
  SettingsItem(
    title = title,
    icon = icon,
    isDanger = isDanger,
    isFirst = isFirst,
    isLast = isLast,
    onClick = onClick
  )
}
