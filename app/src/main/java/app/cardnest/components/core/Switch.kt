package app.cardnest.components.core

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_10

@Composable
fun AppSwitch(checked: Boolean, onCheckedChange: (checked: Boolean) -> Unit) {
  val thumbOffsetX by animateDpAsState(if (checked) 20.dp else 0.dp, label = "")
  val bgColor by animateColorAsState(if (checked) TH_SKY else TH_WHITE_10, label = "")

  Box(
    Modifier
      .width(48.dp)
      .height(28.dp)
      .clip(RoundedCornerShape(14.dp))
      .background(bgColor)
      .clickable(onClick = { onCheckedChange(!checked) })
      .padding(2.dp)
  ) {
    Box(
      Modifier
        .size(24.dp)
        .offset(x = thumbOffsetX)
        .clip(RoundedCornerShape(12.dp))
        .background(TH_WHITE)
    )
  }
}
