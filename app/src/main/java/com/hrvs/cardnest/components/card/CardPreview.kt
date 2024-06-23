package com.hrvs.cardnest.components.card

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.data.CardFocusableField
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.AppTextSize
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_70
import com.hrvs.cardnest.ui.theme.getCardTheme
import com.hrvs.cardnest.utils.card.formatCardViewDetails

@Composable
fun CardPreview(
  card: CardFullProfile,
  usePlaceholders: Boolean = false,
  focused: CardFocusableField? = null,
) {
  val formattedCard = formatCardViewDetails(card, usePlaceholders)

  @Composable
  fun modifierWithAlpha(field: CardFocusableField): Modifier {
    if (!usePlaceholders) return Modifier

    val alphaValue by animateFloatAsState(
      if (focused == field) 1f else 0.6f, tween(300), label = "Field alpha value"
    )

    return Modifier.alpha(alpha = alphaValue)
  }

  Column(
    Modifier
      .aspectRatio(1.586f)
      .clip(RoundedCornerShape(16.dp))
      .background(Brush.linearGradient(getCardTheme(card.theme)))
      .padding(16.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
      Column(modifierWithAlpha(CardFocusableField.CARDHOLDER)) {
        AppText(
          text = "CARDHOLDER",
          size = AppTextSize.XXS,
          color = TH_WHITE_70,
          letterSpacing = (10 / 10).sp,
          useCardFontFamily = true
        )
        AppText(
          text = formattedCard.cardholder,
          size = AppTextSize.LG,
          weight = FontWeight.Medium,
          color = TH_WHITE,
          letterSpacing = (18 / 20).sp,
          useCardFontFamily = true
        )
      }

      if (usePlaceholders || card.issuer.isNotBlank()) {
        Column(modifierWithAlpha(CardFocusableField.ISSUER), horizontalAlignment = Alignment.End) {
          AppText(
            text = "ISSUER",
            size = AppTextSize.XXS,
            color = TH_WHITE_70,
            letterSpacing = (10 / 10).sp,
            useCardFontFamily = true,
          )
          AppText(
            text = formattedCard.issuer,
            size = AppTextSize.LG,
            weight = FontWeight.Medium,
            color = TH_WHITE,
            letterSpacing = (18 / 20).sp,
            useCardFontFamily = true,
          )
        }
      }
    }

    Row(modifierWithAlpha(CardFocusableField.NUMBER)) {
      for (char in formattedCard.number) {
        AppText(
          text = char.toString(),
          modifier = Modifier.width(if (char.isWhitespace()) 8.dp else 16.dp),
          size = AppTextSize.XXL,
          weight = FontWeight.Bold,
          align = TextAlign.Center,
          color = TH_WHITE,
          useCardFontFamily = true,
        )
      }
    }

    Row(modifierWithAlpha(CardFocusableField.EXPIRY).fillMaxWidth(), Arrangement.SpaceBetween) {
      Column {
        AppText(
          text = "VALID THRU",
          size = AppTextSize.XXS,
          color = TH_WHITE_70,
          letterSpacing = (10 / 10).sp,
          useCardFontFamily = true
        )
        Row {
          for (char in formattedCard.expiry) {
            AppText(
              text = char.toString(),
              modifier = Modifier.width(10.dp),
              weight = FontWeight.Bold,
              lineHeight = 28.sp,
              align = TextAlign.Center,
              color = TH_WHITE,
              useCardFontFamily = true,
            )
          }
        }
      }
    }
  }
}
