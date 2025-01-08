package app.cardnest.components.loader

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.ui.theme.TH_WHITE_60

@Composable
fun LoadingIcon(color: Color = TH_WHITE_60, size: Dp = 20.dp) {
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
    tint = color,
    modifier = Modifier.size(size).graphicsLayer(rotationZ = angle.value),
  )
}
