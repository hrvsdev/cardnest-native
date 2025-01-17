package app.cardnest.components.toast

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.TH_BLACK
import app.cardnest.ui.theme.TH_GREEN
import app.cardnest.ui.theme.TH_RED
import app.cardnest.utils.extensions.collectValue
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private val toastState = MutableStateFlow(AppToastData())

@Composable
fun BoxScope.AppToast() {
  val state = toastState.collectValue()

  LaunchedEffect(state) {
    if (state.show) {
      delay(3000)
      AppToast.hide()
    }
  }

  val enter = scaleIn(spring(Spring.DampingRatioLowBouncy, Spring.StiffnessMediumLow)) + fadeIn()
  val exit = scaleOut(targetScale = 0.7f) + fadeOut()

  val color = when (state.type) {
    AppToastType.SUCCESS -> TH_GREEN
    AppToastType.ERROR -> TH_RED
  }

  AnimatedVisibility(
    state.show,
    Modifier.align(Alignment.BottomCenter).navigationBarsPadding().padding(start = 16.dp, end = 16.dp, bottom = 72.dp),
    enter,
    exit
  ) {
    Box(Modifier.background(TH_BLACK, RoundedCornerShape(10.dp)), Alignment.Center) {
      Row(Modifier.padding(horizontal = 14.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        ToastIcon(state.type)
        Spacer(Modifier.size(10.dp))
        AppText(state.message, color = color)
      }
    }
  }
}

@Composable
private fun ToastIcon(type: AppToastType) {
  val color = when (type) {
    AppToastType.SUCCESS -> TH_GREEN
    AppToastType.ERROR -> TH_RED
  }

  val icon = when (type) {
    AppToastType.SUCCESS -> R.drawable.tabler__check
    AppToastType.ERROR -> R.drawable.tabler__x
  }

  Box(Modifier.size(20.dp).background(color, RoundedCornerShape(20.dp)), Alignment.Center) {
    Icon(
      painter = painterResource(icon),
      contentDescription = null,
      tint = TH_BLACK,
      modifier = Modifier.size(16.dp),
    )
  }
}

object AppToast {
  fun success(message: String) {
    toastState.update { it.copy(show = true, message = message, type = AppToastType.SUCCESS) }
  }

  fun error(message: String) {
    toastState.update { it.copy(show = true, message = message, type = AppToastType.ERROR) }
  }

  fun hide() {
    toastState.update { it.copy(show = false) }
  }
}

private data class AppToastData(
  val show: Boolean = false,
  val message: String = "",
  val type: AppToastType = AppToastType.ERROR
)

private enum class AppToastType { SUCCESS, ERROR }
