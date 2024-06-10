package com.hrvs.cardnest.ui.theme

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.LineHeightStyle.Alignment
import androidx.compose.ui.text.style.LineHeightStyle.Trim
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.R

@Composable
fun AppText(
  text: String,
  modifier: Modifier = Modifier,
  size: AppTextSize = AppTextSize.BASE,
  weight: FontWeight = FontWeight.Normal,
  align: TextAlign = TextAlign.Start,
  color: Color = TH_WHITE_80,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  useCardFontFamily: Boolean = false
) {

  val fontSize = when (size) {
    AppTextSize.XXS -> 10.sp
    AppTextSize.XS -> 12.sp
    AppTextSize.SM -> 14.sp
    AppTextSize.BASE -> 16.sp
    AppTextSize.LG -> 18.sp
    AppTextSize.XL -> 20.sp
    AppTextSize.XXL -> 24.sp
    AppTextSize.XXXL -> 30.sp
    AppTextSize.MD -> 17.sp
    AppTextSize.HEADING -> 28.sp
  }

  val actualLineHeight = when (lineHeight) {
    TextUnit.Unspecified -> when (size) {
      AppTextSize.XXS -> 12.sp
      AppTextSize.XS -> 16.sp
      AppTextSize.SM -> 20.sp
      AppTextSize.BASE -> 24.sp
      AppTextSize.LG -> 28.sp
      AppTextSize.XL -> 28.sp
      AppTextSize.XXL -> 32.sp
      AppTextSize.XXXL -> 36.sp
      AppTextSize.MD -> 26.sp
      AppTextSize.HEADING -> 34.sp
    }

    else -> lineHeight
  }


  BasicText(
    text = text,
    modifier = modifier,
    style = TextStyle(
      color = color,
      fontWeight = weight,
      letterSpacing = letterSpacing,
      textAlign = align,
      fontSize = fontSize,
      lineHeight = actualLineHeight,
      lineHeightStyle = LineHeightStyle(Alignment.Proportional, Trim.None),
      fontFamily = if (useCardFontFamily) ManropeFamily else LatoFamily
    )
  )
}

val LatoFamily = FontFamily(
  Font(R.font.lato_light, FontWeight.Light),
  Font(R.font.lato_regular, FontWeight.Normal),
  Font(R.font.lato_bold, FontWeight.Bold),
)

val ManropeFamily = FontFamily(
  Font(R.font.manrope_light, FontWeight.W300),
  Font(R.font.manrope_regular, FontWeight.Normal),
  Font(R.font.manrope_medium, FontWeight.W500),
  Font(R.font.manrope_bold, FontWeight.Bold),
)

enum class AppTextSize {
  XXS, XS, SM, BASE, LG, XL, XXL, XXXL, MD, HEADING
}
