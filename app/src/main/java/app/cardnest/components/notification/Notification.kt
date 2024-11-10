package app.cardnest.components.notification

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.cardnest.data.notification.Notification
import app.cardnest.data.notification.NotificationType
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_RED_10
import app.cardnest.ui.theme.TH_RED_20
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_SKY_10
import app.cardnest.ui.theme.TH_SKY_20

@Composable
fun ColumnScope.Notifications(show: Boolean = false) {
  AnimatedVisibility(show) {
    Column(Modifier.padding(horizontal = 16.dp), Arrangement.spacedBy(8.dp)) {
      Notification(Notification.CARD_DATA_CORRUPTED, true)
      Notification(Notification.AUTH_DATA_CORRUPTED, true)
      Notification(Notification.AUTH_DATA_NOT_UPDATED, true)
    }
  }
}

@Composable
fun ColumnScope.Notification(message: Notification, show: Boolean) {
  val shape = RoundedCornerShape(14.dp)

  val theme = if (message.theme == NotificationType.ERROR) errorTheme else infoTheme

  AnimatedVisibility(show) {
    Box(Modifier.background(theme.bgColor, shape).border(0.5.dp, theme.borderColor, shape)) {
      Box(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
        AppText(
          text = message.message,
          size = AppTextSize.SM,
          color = theme.textColor,
          lineHeight = 18.sp,
          align = TextAlign.Justify
        )
      }
    }
  }
}

private data class NotificationTheme(
  val bgColor: Color,
  val borderColor: Color,
  val textColor: Color
)

private val infoTheme = NotificationTheme(
  bgColor = TH_SKY_10,
  borderColor = TH_SKY_20,
  textColor = TH_SKY
)

private val errorTheme = NotificationTheme(
  bgColor = TH_RED_10,
  borderColor = TH_RED_20,
  textColor = TH_RED
)
