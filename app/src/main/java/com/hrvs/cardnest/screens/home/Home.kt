package com.hrvs.cardnest.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hrvs.cardnest.components.card.CardPreview
import com.hrvs.cardnest.components.containers.ScreenContainer
import com.hrvs.cardnest.components.containers.TabScreenRoot
import com.hrvs.cardnest.components.header.HeaderSearch
import com.hrvs.cardnest.components.header.HeaderTitle
import com.hrvs.cardnest.screens.home.card.CardViewScreen
import com.hrvs.cardnest.state.card.getCards
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.TH_WHITE_60

class HomeScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val cards = getCards(LocalContext.current)

    TabScreenRoot {
      HeaderTitle("Home")
      HeaderSearch()
      ScreenContainer(16.dp) {
        cards.forEach {
          Box(Modifier.clickable { navigator.push(CardViewScreen(it)) }) {
            CardPreview(it.plainData)
          }
        }

        if (cards.isEmpty()) {
          Box(Modifier.fillMaxWidth(), Alignment.Center) {
            AppText("No cards found", Modifier.padding(top = 32.dp), color = TH_WHITE_60)
          }
        }
      }
    }
  }
}
