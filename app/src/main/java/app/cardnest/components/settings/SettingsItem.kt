package app.cardnest.components.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalRippleConfiguration
import androidx.compose.material3.RippleConfiguration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_RED_05
import app.cardnest.ui.theme.TH_RED_12
import app.cardnest.ui.theme.TH_RED_70
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_03
import app.cardnest.ui.theme.TH_WHITE_07
import app.cardnest.ui.theme.TH_WHITE_70

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
  title: String,
  icon: Painter,
  isDanger: Boolean = false,
  isFirst: Boolean = false,
  isLast: Boolean = false,
  isDisabled: Boolean = false,
  onClick: (() -> Unit)? = null,
  rightContent: (@Composable () -> Unit) = {},
) {
  val color = if (isDanger) {
    if (isDisabled) TH_RED_70 else TH_RED
  } else {
    if (isDisabled) TH_WHITE_70 else TH_WHITE
  }

  val bgColor = if (isDanger) {
    if (isDisabled) TH_RED_05 else TH_RED_12
  } else {
    if (isDisabled) TH_WHITE_03 else TH_WHITE_07
  }

  val rippleColor = if (isDanger) TH_RED else TH_WHITE

  val shape = RoundedCornerShape(
    topStart = if (isFirst) 12.dp else 2.dp,
    topEnd = if (isFirst) 12.dp else 2.dp,
    bottomStart = if (isLast) 12.dp else 2.dp,
    bottomEnd = if (isLast) 12.dp else 2.dp
  )

  CompositionLocalProvider(LocalRippleConfiguration provides RippleConfiguration(rippleColor)) {
    Row(
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier
        .clip(shape)
        .clickable(onClick != null, onClick = { onClick?.invoke() })
        .height(44.dp)
        .background(bgColor)
        .padding(horizontal = 12.dp)

    ) {
      Icon(icon, null, Modifier.size(20.dp), color)
      AppText(title, Modifier.weight(1f), color = color)

      rightContent()
    }
  }
}
