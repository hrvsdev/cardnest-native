package com.hrvs.cardnest.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.card.CardPreview
import com.hrvs.cardnest.components.containers.ScreenContainer
import com.hrvs.cardnest.components.containers.TabScreenRoot
import com.hrvs.cardnest.components.header.HeaderSearch
import com.hrvs.cardnest.components.header.HeaderTitle
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.data.PaymentNetwork

val cards = listOf(
  CardFullProfile(
    number = "1234567890123456",
    expiry = "10/28",
    cardholder = "John Doe",
    issuer = "Bank of America",
    network = PaymentNetwork.VISA,
    theme = CardTheme.ROSE
  ),
  CardFullProfile(
    number = "4037720873290682",
    expiry = "12/25",
    cardholder = "Jane Doe",
    issuer = "Chase",
    network = PaymentNetwork.MASTERCARD,
    theme = CardTheme.PURPLE
  )
)

class HomeScreen : Screen {

  @Preview
  @Composable
  override fun Content() {
    TabScreenRoot {
      HeaderTitle("Home")
      HeaderSearch()
      ScreenContainer(16.dp) {
        cards.forEach { CardPreview(it, clickable = true) }
      }
    }
  }
}
