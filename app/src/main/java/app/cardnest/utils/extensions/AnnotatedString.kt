package app.cardnest.utils.extensions

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import app.cardnest.ui.theme.TH_WHITE

fun AnnotatedString.Builder.appendWithEmphasis(text: String, color: Color = TH_WHITE) {
  withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = color)) { append(text) }
}
