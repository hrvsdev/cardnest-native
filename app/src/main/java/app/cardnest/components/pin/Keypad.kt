package app.cardnest.components.pin

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_05
import app.cardnest.ui.theme.TH_WHITE_20

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Keypad(
  onKeyClick: (String) -> Unit,
  onBackspaceClick: () -> Unit,
  isDisabled: Boolean,
  showBiometricsIcon: Boolean = false,
  onBiometricsIconClick: () -> Unit = {},
) {
  val arrangement = Arrangement.spacedBy(20.dp)

  Box(Modifier.fillMaxWidth(), Alignment.Center) {
    FlowRow(maxItemsInEachRow = 3, horizontalArrangement = arrangement, verticalArrangement = arrangement) {
      for (num in 1..9) {
        KeypadNumberButton(num, { onKeyClick(num.toString()) }, isDisabled)
      }

      if (showBiometricsIcon) {
        KeypadIconButton(painterResource(R.drawable.heroicons__finger_print), onBiometricsIconClick, isDisabled)
      } else {
        Spacer(Modifier.size(72.dp))
      }

      KeypadNumberButton(0, { onKeyClick("0") }, isDisabled)
      KeypadIconButton(painterResource(R.drawable.heroicons__backspace), onBackspaceClick, isDisabled)
    }
  }
}

@Composable
fun KeypadButton(onClick: () -> Unit, isDisabled: Boolean = false, content: @Composable BoxScope.() -> Unit) {
  var isBeingPressed by remember { mutableStateOf(false) }

  val alpha by animateFloatAsState(if (isDisabled) 0.5f else 1f, tween(100))
  val scale by animateFloatAsState(if (isBeingPressed) 0.95f else 1f, tween(100))
  val bgColor by animateColorAsState(if (isBeingPressed) TH_WHITE_20 else TH_WHITE_05, tween(100))

  Box(
    contentAlignment = Alignment.Center,
    content = content,
    modifier = Modifier.Companion
      .size(72.dp)
      .graphicsLayer {
        this.alpha = alpha
        scaleX = scale
        scaleY = scale
      }
      .clip(RoundedCornerShape(36.dp))
      .background(bgColor)
      .border(1.dp, TH_WHITE_20, RoundedCornerShape(36.dp))
      .pointerInput(isDisabled) {
        if (isDisabled.not()) {
          detectTapGestures(
            onTap = { onClick() },
            onPress = {
              isBeingPressed = true
              tryAwaitRelease()
              isBeingPressed = false
            },
          )
        }
      }
  )
}

@Composable
fun KeypadNumberButton(number: Int, onClick: () -> Unit, isDisabled: Boolean = false) {
  KeypadButton(onClick, isDisabled) {
    AppText(
      text = number.toString(),
      size = AppTextSize.XXXL,
      color = TH_WHITE,
      useCardFontFamily = true
    )
  }
}

@Composable
fun KeypadIconButton(icon: Painter, onClick: () -> Unit, isDisabled: Boolean = false) {
  KeypadButton(onClick, isDisabled) {
    Icon(icon, contentDescription = null, Modifier.size(30.dp), TH_WHITE)
  }
}
