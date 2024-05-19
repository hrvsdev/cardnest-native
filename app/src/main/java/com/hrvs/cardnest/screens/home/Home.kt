package com.hrvs.cardnest.screens.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.header.HeaderSearch
import com.hrvs.cardnest.components.header.HeaderTitle
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.data.PaymentNetwork
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.AppTextSize
import com.hrvs.cardnest.ui.theme.ScreenContainer
import com.hrvs.cardnest.ui.theme.TH_SKY
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TabScreenRoot

class HomeScreen : Screen {
  @Composable
  override fun Content() {
    TabScreenRoot {
      HeaderTitle("Home")
      HeaderSearch()
      ScreenContainer(16.dp) {
        Button(
          onClick = {},
          modifier = Modifier.height(48.dp).fillMaxWidth(),
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = TH_SKY, contentColor = TH_WHITE)
        ) {
          AppText("Add Card", size = AppTextSize.MD, weight = FontWeight.Bold)
        }
      }
    }
  }
}

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
