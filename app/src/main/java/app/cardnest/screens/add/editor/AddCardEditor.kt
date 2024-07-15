package app.cardnest.screens.add.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import app.cardnest.HomeTab
import app.cardnest.components.button.AppButton
import app.cardnest.components.card.CardEditor
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.data.serializables.CardRecord
import app.cardnest.state.card.CardEditorViewModel
import app.cardnest.state.card.CardsDataViewModel
import app.cardnest.utils.genId
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import org.koin.androidx.compose.koinViewModel

class AddCardEditorScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val tabNavigator = LocalTabNavigator.current

    val cardsDataVM = koinViewModel<CardsDataViewModel>()
    val editorVM = koinViewModel<CardEditorViewModel>()

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
