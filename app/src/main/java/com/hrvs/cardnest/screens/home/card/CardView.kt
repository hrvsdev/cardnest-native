package com.hrvs.cardnest.screens.home.card

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.card.CardPreview
import com.hrvs.cardnest.components.header.SubScreenHeader
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.ui.theme.ScreenContainer
import com.hrvs.cardnest.ui.theme.TabScreenRoot

data class CardViewScreen(val card: CardFullProfile) : Screen {
  @Composable
  override fun Content() {
    TabScreenRoot {
      SubScreenHeader("Card")
      ScreenContainer {
        CardPreview(card)
      }
    }
  }
}
