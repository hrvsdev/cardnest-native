package app.cardnest.components.card

import androidx.compose.foundation.Image
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.data.card.PaymentNetwork
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_WHITE_07

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardNetworkSelector(selectedNetwork: PaymentNetwork, setSelectedNetwork: (PaymentNetwork) -> Unit) {
  val arrangement = Arrangement.spacedBy(8.dp)

  Column {
    AppText("Card network", Modifier.padding(start = 8.dp, bottom = 8.dp))
    FlowRow(maxItemsInEachRow = 3, verticalArrangement = arrangement, horizontalArrangement = arrangement) {
      PaymentNetwork.entries.forEach {
        NetworkButton(it, setSelectedNetwork, selectedNetwork == it)
      }

      Box(Modifier.weight(1f))
      Box(Modifier.weight(1f))
    }
  }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FlowRowScope.NetworkButton(
  network: PaymentNetwork,
  onClick: (PaymentNetwork) -> Unit,
  isSelected: Boolean = false
) {
  Box(
    contentAlignment = Alignment.Center,
    modifier = Modifier
      .weight(1f)
      .height(48.dp)
      .background(TH_WHITE_07, RoundedCornerShape(10.dp))
      .border(1.dp, if (isSelected) TH_SKY else TH_WHITE_07, RoundedCornerShape(10.dp))
      .clickable { onClick(network) }
  ) {
    Image(
      modifier = Modifier.scale(if (network == PaymentNetwork.OTHER) 1.2f else 0.85f),
      contentDescription = network.name,
      painter = painterResource(
        id = when (network) {
          PaymentNetwork.VISA -> R.drawable.visa
          PaymentNetwork.MASTERCARD -> R.drawable.mastercard
          PaymentNetwork.AMEX -> R.drawable.amex
          PaymentNetwork.DISCOVER -> R.drawable.discover
          PaymentNetwork.DINERS -> R.drawable.diners
          PaymentNetwork.RUPAY -> R.drawable.rupay
          PaymentNetwork.OTHER -> R.drawable.tabler__forbid_2
        }
      )
    )
  }
}
