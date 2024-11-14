package app.cardnest.components.card

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import app.cardnest.data.card.CardTheme
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.getCardTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardThemeSelector(selectedTheme: CardTheme, setSelectedTheme: (CardTheme) -> Unit) {
  Column {
    AppText("Card theme", Modifier.padding(start = 8.dp, bottom = 8.dp))
    FlowRow(maxItemsInEachRow = 3, verticalArrangement = Arrangement.spacedBy(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
      CardTheme.entries.forEach {
        ThemeButton(it, setSelectedTheme, selectedTheme == it)
      }
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowScope.ThemeButton(theme: CardTheme, onClick: (CardTheme) -> Unit, isSelected: Boolean = false) {
  val transition = updateTransition(isSelected, "Card theme state")

  val padding by transition.animateDp(label = "Card theme padding state") {
    if (it) 6.dp else 0.dp
  }

  val radius by transition.animateDp(label = "Card theme radius") {
    if (it) 6.dp else 10.dp
  }

  val gradient = Brush.linearGradient(getCardTheme(theme))

  Box(
    Modifier
      .weight(1f)
      .height(48.dp)
      .border(1.dp, gradient, RoundedCornerShape(10.dp))
      .padding(padding)
      .clickable { onClick(theme) }
      .background(gradient, RoundedCornerShape(radius))
  )
}
