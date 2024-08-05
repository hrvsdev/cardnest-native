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
import androidx.compose.runtime.MutableState
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
import app.cardnest.screens.pin.create.PIN_LENGTH
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_05
import app.cardnest.ui.theme.TH_WHITE_20

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Keypad(
  pin: MutableState<String>,
  onPinChange: (String) -> Unit,
  onPinSubmit: () -> Unit,
  showBiometricIcon: Boolean = false,
  onBiometricIconClick: () -> Unit = {},
) {
  val isDisabled = pin.value.length == PIN_LENGTH

  fun onPinButtonClick(number: Int) {
    if (pin.value.length < PIN_LENGTH) onPinChange(pin.value + number)
    if (pin.value.length == PIN_LENGTH) onPinSubmit()
  }

  fun onBackspaceButtonClick() {
    pin.value = pin.value.dropLast(1)
  }

  Box(Modifier.fillMaxWidth(), Alignment.Center) {
    FlowRow(
      maxItemsInEachRow = 3,
      verticalArrangement = Arrangement.spacedBy(20.dp),
      horizontalArrangement = Arrangement.spacedBy(20.dp),
    ) {

      for (num in 1..9) {
        KeypadNumberButton(num, { onPinButtonClick(num) }, isDisabled)
      }

      if (showBiometricIcon) {
        KeypadIconButton(
          icon = painterResource(R.drawable.heroicons__finger_print),
          onClick = onBiometricIconClick,
          disabled = isDisabled
        )
      } else {
        Spacer(Modifier.size(72.dp))
      }

      KeypadNumberButton(0, { onPinButtonClick(0) }, isDisabled)
      KeypadIconButton(
        icon = painterResource(R.drawable.heroicons__backspace),
        onClick = ::onBackspaceButtonClick,
        disabled = isDisabled
      )
    }
  }
}

@Composable
fun KeypadButton(
  onClick: () -> Unit,
  disabled: Boolean = false,
  content: @Composable BoxScope.() -> Unit,
) {
  var isBeingPressed by remember { mutableStateOf(false) }

  val scale by animateFloatAsState(
    targetValue = if (isBeingPressed) 0.95f else 1f,
    animationSpec = tween(100),
    label = ""
  )

  val bgColor by animateColorAsState(
    targetValue = if (isBeingPressed) TH_WHITE_20 else TH_WHITE_05,
    animationSpec = tween(100),
    label = ""
  )

  Box(
    contentAlignment = Alignment.Center,
    content = content,
    modifier = Modifier.Companion
      .size(72.dp)
      .graphicsLayer {
        alpha = if (disabled) 0.5f else 1f
        scaleX = scale
        scaleY = scale
      }
      .clip(RoundedCornerShape(36.dp))
      .background(bgColor)
      .border(1.dp, TH_WHITE_20, androidx.compose.foundation.shape.RoundedCornerShape(36.dp))
      .pointerInput(true) {
        detectTapGestures(
          onTap = { if (!disabled) onClick() },
          onPress = {
            if (disabled) return@detectTapGestures
            isBeingPressed = true
            tryAwaitRelease()
            isBeingPressed = false
          },
        )
      }
  )
}

@Composable
fun KeypadNumberButton(number: Int, onClick: () -> Unit, disabled: Boolean = false) {
  KeypadButton(onClick, disabled) {
    AppText(
      text = number.toString(),
      size = AppTextSize.XXXL,
      color = TH_WHITE,
      useCardFontFamily = true
    )
  }
}

@Composable
fun KeypadIconButton(icon: Painter, onClick: () -> Unit, disabled: Boolean = false) {
  KeypadButton(onClick, disabled) {
    Icon(icon, contentDescription = null, Modifier.size(30.dp), TH_WHITE)
  }
}
