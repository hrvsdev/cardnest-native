package app.cardnest.components.settings

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.loader.LoadingIcon
import app.cardnest.ui.theme.TH_RED_50
import app.cardnest.ui.theme.TH_RED_60
import app.cardnest.ui.theme.TH_WHITE_50
import app.cardnest.ui.theme.TH_WHITE_60

@Composable
fun SettingsButton(
  title: String,
  icon: Painter,
  isDanger: Boolean = false,
  isFirst: Boolean = false,
  isLast: Boolean = false,
  isLoading: Boolean = false,
  isExternalLink: Boolean = false,
  onClick: () -> Unit,
) {
  val color = if (isDanger) {
    if (isLoading) TH_RED_50 else TH_RED_60
  } else {
    if (isLoading) TH_WHITE_50 else TH_WHITE_60
  }

  SettingsItem(
    title = title,
    icon = icon,
    isDanger = isDanger,
    isFirst = isFirst,
    isLast = isLast,
    isDisabled = isLoading,
    onClick = onClick,
    rightContent = {
      if (isLoading) {
        LoadingIcon(color)
      } else {
        ChevronRightIcon(color, isExternalLink)
      }
    }
  )
}

@Composable
private fun ChevronRightIcon(color: Color, isExternalLink: Boolean) {
  Icon(
    painter = painterResource(if (isExternalLink) R.drawable.tabler__arrow_up_right else R.drawable.tabler__chevron_right),
    contentDescription = null,
    modifier = Modifier.size(20.dp),
    tint = color
  )
}
