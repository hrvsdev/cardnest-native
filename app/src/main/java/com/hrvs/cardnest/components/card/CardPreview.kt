package com.hrvs.cardnest.components.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.AppTextSize
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_70
import com.hrvs.cardnest.ui.theme.getCardTheme
import com.hrvs.cardnest.utils.formatCardViewDetails

@Composable
fun CardPreview(card: CardFullProfile, usePlaceholders: Boolean = false) {

  val formattedCard = formatCardViewDetails(card, usePlaceholders)

  Column(
    Modifier
      .aspectRatio(1.586f)
      .clip(RoundedCornerShape(16.dp))
      .background(Brush.linearGradient(getCardTheme(card.theme)))
      .clickable {  }
      .padding(16.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Column {
        AppText(
          text = "CARDHOLDER",
          size = AppTextSize.XXS,
          color = TH_WHITE_70,
          letterSpacing = (10 / 10).sp
        )
        AppText(
          text = formattedCard.cardholder,
          size = AppTextSize.LG,
          weight = FontWeight.Medium,
          color = TH_WHITE,
          letterSpacing = (18 / 20).sp
        )
      }

      Column(horizontalAlignment = Alignment.End) {
        AppText(
          text = "ISSUER", size = AppTextSize.XXS, color = TH_WHITE_70, letterSpacing = (10 / 10).sp
        )
        AppText(
          text = formattedCard.issuer,
          size = AppTextSize.LG,
          weight = FontWeight.Medium,
          color = TH_WHITE,
          letterSpacing = (18 / 20).sp
        )
      }
    }

    Row {
      for (char in formattedCard.number) {
        AppText(
          text = char.toString(),
          modifier = Modifier.width(if (char.toString().trim() == "") 8.dp else 16.dp),
          size = AppTextSize.XXL,
          weight = FontWeight.Bold,
          align = TextAlign.Center,
          color = TH_WHITE
        )
      }
    }

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Column {
        AppText(
          text = "VALID THRU",
          size = AppTextSize.XXS,
          color = TH_WHITE_70,
          letterSpacing = (10 / 10).sp
        )
        Row {
          for (char in formattedCard.expiry) {
            AppText(
              text = char.toString(),
              modifier = Modifier.width(10.dp),
              weight = FontWeight.Bold,
              lineHeight = 28.sp,
              align = TextAlign.Center,
              color = TH_WHITE
            )
          }
        }
      }
    }
  }
}
