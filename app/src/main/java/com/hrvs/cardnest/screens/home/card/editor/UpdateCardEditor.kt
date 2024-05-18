package com.hrvs.cardnest.screens.home.card.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.card.CardEditor
import com.hrvs.cardnest.components.header.SubScreenHeader
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.state.card.CardEditorViewModel
import com.hrvs.cardnest.ui.theme.ScreenContainer
import com.hrvs.cardnest.ui.theme.TabScreenRoot

data class UpdateCardEditorScreen(val card: CardFullProfile) : Screen {
  @Composable
  override fun Content() {
    val viewModel = remember { CardEditorViewModel(card) }

    TabScreenRoot {
      SubScreenHeader("Edit Card", rightButtonLabel = "Done")
      ScreenContainer(32.dp) {
        CardEditor(viewModel)
      }
    }
  }
}
