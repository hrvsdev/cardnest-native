package app.cardnest.components.button

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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

  disableIfOffline: Boolean = false,
) {
  val ctx = LocalContext.current
  val isDisabledOffline = disableIfOffline && connectionState.collectAsStateWithLifecycle().value.shouldWrite.not()

  val containerColor = when (variant) {
    ButtonVariant.Solid -> when (theme) {
      ButtonTheme.Primary -> if (isDisabledOffline) TH_SKY_20 else TH_SKY
      ButtonTheme.Danger -> if (isDisabledOffline) TH_RED_20 else TH_RED
    }

    ButtonVariant.Flat -> when (theme) {
      ButtonTheme.Primary -> TH_SKY_10
      ButtonTheme.Danger -> TH_RED_10
    }
  }

  val textAndContentColor = when (variant) {
    ButtonVariant.Solid -> if (isDisabledOffline) TH_WHITE_70 else TH_WHITE
    ButtonVariant.Flat -> when (theme) {
      ButtonTheme.Primary -> TH_SKY
      ButtonTheme.Danger -> TH_RED
    }
  }

  fun onButtonClick() {
    if (isDisabledOffline) {
      Toast.makeText(ctx, "You are offline", Toast.LENGTH_SHORT).show()
    } else {
      onClick()
    }
  }

  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Button(
      onClick = ::onButtonClick,
      modifier = Modifier
        .height(48.dp)
        .fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = ButtonDefaults.buttonColors(containerColor, contentColor = textAndContentColor)
    ) {
      AppText(title, size = AppTextSize.MD, weight = FontWeight.Bold, color = textAndContentColor)
    }
  }
}

enum class ButtonTheme { Primary, Danger }
enum class ButtonVariant { Solid, Flat }
