package app.cardnest.ui.theme

import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
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
import app.cardnest.R

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
  BasicText(
    text = text,
    modifier = modifier,
    style = TextStyle(
      color = color,
      fontWeight = weight,
      letterSpacing = letterSpacing,
      textAlign = align,
      fontSize = size.fontSize,
      lineHeight = if (lineHeight == TextUnit.Unspecified) size.lineHeight else lineHeight,
      lineHeightStyle = LineHeightStyle(Alignment.Center, Trim.None),
      fontFamily = if (useCardFontFamily) ManropeFamily else LatoFamily
    )
  )
}

@Composable
fun AppText(
  text: AnnotatedString,
  modifier: Modifier = Modifier,
  size: AppTextSize = AppTextSize.BASE,
  weight: FontWeight = FontWeight.Normal,
  align: TextAlign = TextAlign.Start,
  color: Color = TH_WHITE_80,
  letterSpacing: TextUnit = TextUnit.Unspecified,
  lineHeight: TextUnit = TextUnit.Unspecified,
  useCardFontFamily: Boolean = false
) {
  BasicText(
    text = text,
    modifier = modifier,
    style = TextStyle(
      color = color,
      fontWeight = weight,
      letterSpacing = letterSpacing,
      textAlign = align,
      fontSize = size.fontSize,
      lineHeight = if (lineHeight == TextUnit.Unspecified) size.lineHeight else lineHeight,
      lineHeightStyle = LineHeightStyle(Alignment.Center, Trim.None),
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

enum class AppTextSize(val fontSize: TextUnit, val lineHeight: TextUnit) {
  XXS(10.sp, 12.sp),
  XS(12.sp, 16.sp),
  SM(14.sp, 20.sp),
  BASE(16.sp, 24.sp),
  LG(18.sp, 28.sp),
  XL(20.sp, 28.sp),
  XXL(24.sp, 32.sp),
  XXXL(30.sp, 36.sp),
  MD(17.sp, 26.sp),
  HEADING(28.sp, 34.sp)
}
