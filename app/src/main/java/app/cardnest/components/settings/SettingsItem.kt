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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_RED_12
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_07

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsItem(
  title: String,
  icon: Painter,
  isDanger: Boolean = false,
  isFirst: Boolean = false,
  isLast: Boolean = false,
  rightContent: (@Composable () -> Unit)? = null,
  onClick: (() -> Unit)? = null
) {
  val color = if (isDanger) TH_RED else TH_WHITE
  val shape = RoundedCornerShape(
    topStart = if (isFirst) 12.dp else 2.dp,
    topEnd = if (isFirst) 12.dp else 2.dp,
    bottomStart = if (isLast) 12.dp else 2.dp,
    bottomEnd = if (isLast) 12.dp else 2.dp
  )

  CompositionLocalProvider(LocalRippleConfiguration provides RippleConfiguration(color = color)) {
    Row(
      modifier = Modifier
        .clip(shape)
        .clickable(enabled = onClick != null, onClick = { onClick?.invoke() })
        .height(44.dp)
        .background(if (isDanger) TH_RED_12 else TH_WHITE_07)
        .padding(horizontal = 12.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(icon, null, Modifier.size(20.dp), color)
      AppText(title, Modifier.weight(1f), color = color)

      if (rightContent == null) {
        Icon(
          painter = painterResource(R.drawable.tabler__chevron_right),
          contentDescription = null,
          modifier = Modifier.size(20.dp),
          tint = color
        )
      } else {
        rightContent()
      }
    }
  }
}