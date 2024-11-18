package app.cardnest.components.button

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.components.loader.LoadingIcon
import app.cardnest.components.toast.AppToast
import app.cardnest.data.connectionState
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
  isDisabledIfOffline: Boolean = false,
  isDisabled: Boolean = false,
) {
  val isOffline = isDisabledIfOffline && connectionState.collectAsStateWithLifecycle().value.shouldWrite.not()

  val isFullyDisabled = isDisabled || isLoading
  val isVisuallyDisabled = isFullyDisabled || isOffline

  val interactionSource = remember { MutableInteractionSource() }

  val containerColor = when (variant) {
    ButtonVariant.Solid -> when (theme) {
      ButtonTheme.Primary -> if (isVisuallyDisabled) TH_SKY_20 else TH_SKY
      ButtonTheme.Danger -> if (isVisuallyDisabled) TH_RED_20 else TH_RED
    }

    ButtonVariant.Flat -> when (theme) {
      ButtonTheme.Primary -> TH_SKY_10
      ButtonTheme.Danger -> TH_RED_10
    }
  }

  val textAndContentColor = when (variant) {
    ButtonVariant.Solid -> if (isVisuallyDisabled) TH_WHITE_70 else TH_WHITE
    ButtonVariant.Flat -> when (theme) {
      ButtonTheme.Primary -> TH_SKY
      ButtonTheme.Danger -> TH_RED
    }
  }

  fun onButtonClick() {
    if (isOffline) {
      AppToast.error("You are offline")
      return
    }

    onClick()
  }

  fun Modifier.button() = this.clickable(
    onClick = ::onButtonClick,
    interactionSource = interactionSource,
    indication = ripple(color = { textAndContentColor }),
    enabled = isFullyDisabled.not(),
    role = Role.Button
  )

  Box(Modifier.height(48.dp).fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(containerColor).button(), Alignment.Center) {
    if (isLoading) {
      LoadingIcon(color = textAndContentColor, size = 26.dp)
    } else {
      AppText(title, size = AppTextSize.MD, weight = FontWeight.Bold, color = textAndContentColor)
    }
  }
}

enum class ButtonTheme { Primary, Danger }
enum class ButtonVariant { Solid, Flat }
