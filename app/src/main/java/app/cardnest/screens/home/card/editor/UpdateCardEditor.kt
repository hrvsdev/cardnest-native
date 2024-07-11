package app.cardnest.screens.home.card.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import app.cardnest.LocalCardsDataVM
import app.cardnest.components.button.AppButton
import app.cardnest.components.card.CardEditor
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.data.serializables.CardRecord
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.state.appViewModelFactory
import app.cardnest.state.card.CardEditorViewModel

data class UpdateCardEditorScreen(val cardRecord: CardRecord) : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val cardsDataVM = LocalCardsDataVM.current

    val editorVM = viewModel<CardEditorViewModel>(
      factory = appViewModelFactory { CardEditorViewModel(cardRecord.plainData) }
    )

    fun update() {
      editorVM.onSubmit {
        val updatedCard = CardRecord(cardRecord.id, it)

        if (updatedCard == cardRecord) {
          navigator.pop()
          return@onSubmit
        }

        cardsDataVM.updateCard(updatedCard)
        navigator.replaceAll(listOf(HomeScreen(), CardViewScreen(updatedCard)))
      }
    }

    SubScreenRoot(
      title = "Edit Card",
      rightButtonLabel = "Done",
      onRightButtonClick = ::update,
      spacedBy = 32.dp
    ) {
      CardEditor(editorVM)
      AppButton(title = "Update", onClick = ::update)
    }
  }
}
