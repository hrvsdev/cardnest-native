package com.hrvs.cardnest.screens.home.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.card.CardPreview
import com.hrvs.cardnest.components.core.AppTextField
import com.hrvs.cardnest.components.header.SubScreenHeader
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.ui.theme.ScreenContainer
import com.hrvs.cardnest.ui.theme.TabScreenRoot

data class CardViewScreen(val card: CardFullProfile) : Screen {
  @Composable
  override fun Content() {
    val (number, onNumberChange) = remember { mutableStateOf("") }

    TabScreenRoot {
      SubScreenHeader("Card")
      ScreenContainer(32.dp) {
        CardPreview(card)
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
          AppTextField(
            label = "Card number",
            placeholder = "Enter card number",
            value = number,
            onValueChange = onNumberChange,
            maxLength = 16,
          )
        }
      }
    }
  }
}
