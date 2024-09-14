package app.cardnest.components.settings

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.ui.theme.TH_WHITE_50

@Composable
fun SettingsButton(
  title: String,
  icon: Painter,
  isDanger: Boolean = false,
  isFirst: Boolean = false,
  isLast: Boolean = false,
  isLoading: Boolean = false,
  onClick: () -> Unit,
) {
  SettingsItem(
    title = title,
    icon = icon,
    isDanger = isDanger,
    isFirst = isFirst,
    isLast = isLast,
    isDisabled = isLoading,
    onClick = onClick,
    rightContent = if (isLoading) {
      { LoadingIcon() }
    } else null
  )
}

@Composable
private fun LoadingIcon() {
  val infiniteTransition = rememberInfiniteTransition(label = "Loading")
  val angle = infiniteTransition.animateFloat(
    initialValue = 0F,
    targetValue = 360F,
    animationSpec = infiniteRepeatable(keyframes { durationMillis = 1000 }),
    label = "Loading",
  )

  Icon(
    painter = painterResource(id = R.drawable.tabler__loader_2),
    contentDescription = null,
    tint = TH_WHITE_50,
    modifier = Modifier
      .size(20.dp)
      .graphicsLayer(rotationZ = angle.value),
  )
}
