package app.cardnest.components.settings

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import app.cardnest.components.core.AppSwitch

@Composable
fun SettingsSwitch(
  title: String,
  icon: Painter,
  checked: Boolean,
  onCheckedChange: (checked: Boolean) -> Unit,
  isFirst: Boolean = false,
  isLast: Boolean = false,
) {
  SettingsItem(
    title = title,
    icon = icon,
    isFirst = isFirst,
    isLast = isLast,
    rightContent = {
      AppSwitch(checked, onCheckedChange)
    }
  )
}
