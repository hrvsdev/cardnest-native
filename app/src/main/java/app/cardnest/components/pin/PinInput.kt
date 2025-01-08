package app.cardnest.components.pin

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import app.cardnest.screens.pin.create.create.PIN_LENGTH
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_00
import kotlinx.coroutines.delay

@Composable
fun PinInput(pin: String, hasError: Boolean = false, isLoading: Boolean = false) {
  var animatingIndex by remember { mutableIntStateOf(-1) }
  val offsetXAnimation = remember { Animatable(0f) }

  LaunchedEffect(isLoading) {
    if (!isLoading) {
      animatingIndex = -1
      return@LaunchedEffect
    }

    animatingIndex = 0
    while (true) {
      delay(100)
      animatingIndex = (animatingIndex + 1) % PIN_LENGTH
    }
  }

  LaunchedEffect(hasError) {
    val shakeDistance = 8f
    val duration = 100

    if (hasError) {
      repeat(3) {
        offsetXAnimation.animateTo(-shakeDistance, tween(duration))
        offsetXAnimation.animateTo(shakeDistance, tween(duration))
      }
      offsetXAnimation.animateTo(0f, tween(duration / 2))
    } else {
      offsetXAnimation.animateTo(0f, tween(duration))
    }
  }

  Row(Modifier.offset(offsetXAnimation.value.dp), Arrangement.spacedBy(12.dp)) {
    repeat(PIN_LENGTH) {
      Dot(
        isFilled = it < pin.length,
        hasError = hasError,
        isAnimating = it == animatingIndex
      )
    }
  }
}

@Composable
private fun Dot(isFilled: Boolean, hasError: Boolean, isAnimating: Boolean) {
  val scale by animateFloatAsState(
    targetValue = if (isAnimating) 1.5f else 1f,
    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow),
  )

  Box(
    Modifier
      .size(12.dp)
      .scale(scale)
      .border(1.dp, if (hasError) TH_RED else TH_WHITE, RoundedCornerShape(6.dp))
      .background(if (hasError) TH_RED else if (isFilled) TH_WHITE else TH_WHITE_00, RoundedCornerShape(6.dp))
  )
}
