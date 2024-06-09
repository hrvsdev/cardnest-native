package com.hrvs.cardnest.screens.add.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.button.AppButton
import com.hrvs.cardnest.components.card.CardEditor
import com.hrvs.cardnest.components.containers.SubScreenRoot
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.data.PaymentNetwork
import com.hrvs.cardnest.state.card.CardEditorViewModel

class AddCardEditorScreen : Screen {
  @Composable
  override fun Content() {
    val defaultCard = CardFullProfile(
      number = "",
      expiry = "",
      cardholder = "",
      issuer = "",
      network = PaymentNetwork.VISA,
      theme = CardTheme.entries.random(),
    )

    val viewModel = remember { CardEditorViewModel(defaultCard) }

    SubScreenRoot(title = "New Card", rightButtonLabel = "Done", spacedBy = 32.dp) {
      CardEditor(viewModel)
      AppButton(title = "Save", onClick = { /*TODO*/ })
    }
  }
}
