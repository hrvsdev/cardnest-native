package com.hrvs.cardnest.components.button

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.AppTextSize
import com.hrvs.cardnest.ui.theme.TH_RED
import com.hrvs.cardnest.ui.theme.TH_RED_10
import com.hrvs.cardnest.ui.theme.TH_SKY
import com.hrvs.cardnest.ui.theme.TH_SKY_10
import com.hrvs.cardnest.ui.theme.TH_WHITE

@Composable
fun AppButton(
  title: String,
  onClick: () -> Unit,

  theme: ButtonTheme = ButtonTheme.Primary,
  variant: ButtonVariant = ButtonVariant.Solid
) {

  val containerColor = when (variant) {
    ButtonVariant.Solid -> when (theme) {
      ButtonTheme.Primary -> TH_SKY
      ButtonTheme.Danger -> TH_RED
    }

    ButtonVariant.Flat -> when (theme) {
      ButtonTheme.Primary -> TH_SKY_10
      ButtonTheme.Danger -> TH_RED_10
    }
  }

  val textAndContentColor = when (variant) {
    ButtonVariant.Solid -> TH_WHITE
    ButtonVariant.Flat -> when (theme) {
      ButtonTheme.Primary -> TH_SKY
      ButtonTheme.Danger -> TH_RED
    }
  }

  Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Button(
      onClick = onClick,
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