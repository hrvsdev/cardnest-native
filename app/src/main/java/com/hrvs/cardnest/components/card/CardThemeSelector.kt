package com.hrvs.cardnest.components.card

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.ui.theme.TH_WHITE_00
import com.hrvs.cardnest.ui.theme.TH_WHITE_80
import com.hrvs.cardnest.ui.theme.getCardTheme

@Preview(showBackground = true, showSystemUi = true, backgroundColor = 0xFF000000)
@Composable
fun CardThemeSelector() {
  val (selectedTheme, setSelectedTheme) = remember { mutableStateOf(CardTheme.entries.random()) }

  LazyVerticalGrid(
    columns = GridCells.Fixed(3),
    verticalArrangement = Arrangement.spacedBy(8.dp),
    horizontalArrangement = Arrangement.spacedBy(8.dp),
    modifier = Modifier
      .padding(16.dp)
      .fillMaxHeight()
  ) {
    items(CardTheme.entries.size) {
      GridItem(
        CardTheme.entries[it],
        onPress = { theme -> setSelectedTheme(theme) },
        isSelected = selectedTheme == CardTheme.entries[it]
      )
    }
  }
}

@Composable
fun GridItem(
  theme: CardTheme,
  onPress: (theme: CardTheme) -> Unit,
  isSelected: Boolean = false
) {
  val transition = updateTransition(isSelected, "Card theme state")

  val padding by transition.animateDp(label = "Card theme padding state") {
    if (it) 8.dp else 0.dp
  }

  val borderColor by transition.animateColor(label = "Card theme border color") {
    if (it) TH_WHITE_80 else TH_WHITE_00
  }

  val radius by transition.animateDp(label = "Card theme radius") {
    if (it) 10.dp else 8.dp
  }

  Box(
   Modifier
      .height(48.dp)
      .border(1.dp, borderColor, RoundedCornerShape(10.dp))
      .clickable { onPress(theme) }
      .padding(padding)
      .background(Brush.linearGradient(getCardTheme(theme)), RoundedCornerShape(radius))
  )
}
