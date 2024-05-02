package com.hrvs.cardnest

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.components.card.CardPreview
import com.hrvs.cardnest.components.header.HeaderSearch
import com.hrvs.cardnest.components.header.HeaderTitle
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.data.PaymentNetwork
import com.hrvs.cardnest.ui.theme.ScreenContainer
import com.hrvs.cardnest.ui.theme.TabScreenRoot

@Preview
@Composable
fun App() {
  TabScreenRoot {
    HeaderTitle("Home")
    HeaderSearch()
    ScreenContainer(16.dp) {
      CardPreview(
        CardFullProfile(
          number = "1234567890123456",
          expiry = "10/28",
          cardholder = "John Doe",
          issuer = "Bank of America",
          network = PaymentNetwork.VISA,
          theme = CardTheme.ROSE
        )
      )
      CardPreview(
        CardFullProfile(
          number = "4037720873290682",
          expiry = "12/25",
          cardholder = "Jane Doe",
          issuer = "Chase",
          network = PaymentNetwork.MASTERCARD,
          theme = CardTheme.PURPLE
        )
      )
    }
  }
}
