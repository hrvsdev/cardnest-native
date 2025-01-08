package app.cardnest.components.button

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cardnest.components.loader.LoadingIcon
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_RED_10
import app.cardnest.ui.theme.TH_RED_20
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_SKY_10
import app.cardnest.ui.theme.TH_SKY_20
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_70

@Composable
fun AppButton(
  title: String,
  onClick: () -> Unit,

  theme: ButtonTheme = ButtonTheme.Primary,
  variant: ButtonVariant = ButtonVariant.Solid,

  isLoading: Boolean = false,
  isDisabled: Boolean = isLoading,
) {
  val interactionSource = remember { MutableInteractionSource() }

  val containerColor by animateColorAsState(
    when (variant) {
      ButtonVariant.Solid -> when (theme) {
        ButtonTheme.Primary -> if (isDisabled) TH_SKY_20 else TH_SKY
        ButtonTheme.Danger -> if (isDisabled) TH_RED_20 else TH_RED
      }

      ButtonVariant.Flat -> when (theme) {
        ButtonTheme.Primary -> TH_SKY_10
        ButtonTheme.Danger -> TH_RED_10
      }
    }
  )

  val textAndContentColor by animateColorAsState(
    when (variant) {
      ButtonVariant.Solid -> if (isDisabled) TH_WHITE_70 else TH_WHITE
      ButtonVariant.Flat -> when (theme) {
        ButtonTheme.Primary -> TH_SKY
        ButtonTheme.Danger -> TH_RED
      }
    }
  )

  fun Modifier.button() = this.clickable(
    onClick = onClick,
    interactionSource = interactionSource,
    indication = ripple(color = { textAndContentColor }),
    enabled = isDisabled.not(),
    role = Role.Button
  )

  val enter = fadeIn() + expandHorizontally() + slideInHorizontally { it }
  val exit = fadeOut() + shrinkHorizontally() + slideOutHorizontally { it }

  Box(Modifier.height(48.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(containerColor).button()) {
    Row(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically) {
      AnimatedVisibility(isLoading, enter = enter, exit = exit) {
        LoadingIcon(color = TH_WHITE_70)
      }

      AnimatedVisibility(isLoading, enter = expandHorizontally(), exit = shrinkHorizontally()) {
        Spacer(Modifier.size(8.dp))
      }

      AppText(title, size = AppTextSize.MD, weight = FontWeight.Bold, color = textAndContentColor)
    }
  }
}

enum class ButtonTheme { Primary, Danger }
enum class ButtonVariant { Solid, Flat }
