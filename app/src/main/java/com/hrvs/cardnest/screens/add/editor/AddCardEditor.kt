package com.hrvs.cardnest.screens.add.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.hrvs.cardnest.HomeTab
import com.hrvs.cardnest.LocalCardsDataVM
import com.hrvs.cardnest.components.button.AppButton
import com.hrvs.cardnest.components.card.CardEditor
import com.hrvs.cardnest.components.containers.SubScreenRoot
import com.hrvs.cardnest.data.serializables.CardRecord
import com.hrvs.cardnest.state.card.CardEditorViewModel
import com.hrvs.cardnest.utils.genId

class AddCardEditorScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val tabNavigator = LocalTabNavigator.current
    val cardsDataVM = LocalCardsDataVM.current

    val editorVM = viewModel<CardEditorViewModel>()

    fun saveCard() {
      editorVM.onSubmit {
        val id = genId()
        val card = CardRecord(id, it)

        cardsDataVM.addCard(card)

        tabNavigator.current = HomeTab
        navigator.popUntilRoot()
      }
    }

    SubScreenRoot(
      title = "New Card",
      rightButtonLabel = "Done",
      onRightButtonClick = ::saveCard,
      spacedBy = 32.dp
    ) {
      CardEditor(editorVM)
      AppButton(title = "Save", onClick = ::saveCard)
    }
  }
}
