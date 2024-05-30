package com.hrvs.cardnest.screens.home.card.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.button.AppButton
import com.hrvs.cardnest.components.card.CardEditor
import com.hrvs.cardnest.components.containers.SubScreenRoot
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.state.card.CardEditorViewModel

data class UpdateCardEditorScreen(val card: CardFullProfile) : Screen {
  @Composable
  override fun Content() {
    val viewModel = remember { CardEditorViewModel(card) }

    SubScreenRoot(
      title = "Card",
      rightButtonLabel = "Edit",
      spacedBy = 32.dp
    ) {
      CardEditor(viewModel)
      AppButton(title = "Update", onClick = { /*TODO*/ })
    }
  }
}
