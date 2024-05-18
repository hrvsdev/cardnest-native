package com.hrvs.cardnest.screens.home.card.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.card.CardEditor
import com.hrvs.cardnest.components.header.SubScreenHeader
import com.hrvs.cardnest.data.CardFocusableField
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.data.PaymentNetwork
import com.hrvs.cardnest.ui.theme.ScreenContainer
import com.hrvs.cardnest.ui.theme.TabScreenRoot
import com.hrvs.cardnest.utils.card.addCardNumberSpaces

data class UpdateCardEditorScreen(val card: CardFullProfile) : Screen {
  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  override fun Content() {
    TabScreenRoot {
      SubScreenHeader("Edit Card", rightButtonLabel = "Done")
      ScreenContainer(32.dp) {
        val number = rememberTextFieldState(addCardNumberSpaces(card.number))
        val expiry = rememberTextFieldState(card.expiry)
        val cardholder = rememberTextFieldState(card.cardholder)
        val issuer = rememberTextFieldState(card.issuer)

        val (network, onNetworkChange) = remember { mutableStateOf(card.network) }
        val (theme, onThemeChange) = remember { mutableStateOf(card.theme) }

        val (focused, onFocusedChange) = remember { mutableStateOf<CardFocusableField?>(null) }

        CardEditor(
          number = number,
          expiry = expiry,
          cardholder = cardholder,
          issuer = issuer,

          network = network,
          onNetworkChange = onNetworkChange,

          theme = theme,
          onThemeChange = onThemeChange,

          focused = focused,
          onFocusedChange = onFocusedChange,
        )
      }
    }
  }
}
