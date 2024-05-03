package com.hrvs.cardnest.components.header

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.TH_SKY
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_10

@Composable
fun SubScreenHeader(
  title: String,

  leftIconLabel: String? = null,

  rightButtonLabel: String? = null,
  rightButtonIcon: ImageVector? = null,
  onRightButtonClick: () -> Unit = {},
) {
  Column {
    Box(Modifier.fillMaxWidth(), Alignment.Center) {
      AppText(
        text = title,
        color = TH_WHITE,
        weight = FontWeight.Bold,
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(2.dp),
          modifier = Modifier
            .height(48.dp)
            .clickable { }
            .padding(horizontal = 16.dp)
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Outlined.KeyboardArrowLeft,
            contentDescription = null,
            tint = TH_SKY,
            modifier = Modifier.size(24.dp)
          )
          AppText(
            text = leftIconLabel ?: "Back",
            color = TH_SKY,
          )
        }

        Row(
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.spacedBy(4.dp),
          modifier = Modifier
            .height(48.dp)
            .clickable(onClick = onRightButtonClick)
            .padding(start = 16.dp, end = 20.dp)
        ) {

          if (rightButtonIcon != null) {
            Icon(
              imageVector = rightButtonIcon,
              contentDescription = null,
              tint = TH_SKY,
              modifier = Modifier.size(18.dp)
            )
          }

          if (rightButtonLabel != null) {
            AppText(text = rightButtonLabel, color = TH_SKY)
          }
        }
      }
    }

    HorizontalDivider(thickness = 0.5.dp, color = TH_WHITE_10)
  }
}
