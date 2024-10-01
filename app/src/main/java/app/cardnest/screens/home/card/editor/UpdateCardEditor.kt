package app.cardnest.screens.home.card.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import app.cardnest.components.button.AppButton
import app.cardnest.components.card.CardEditor
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.data.card.CardEditorViewModel
import app.cardnest.data.card.CardUnencrypted
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class UpdateCardEditorScreen(val card: CardUnencrypted) : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val updateCardVM = koinViewModel<UpdateCardViewModel> { parametersOf(card.id, navigator) }
    val editorVM = koinViewModel<CardEditorViewModel> { parametersOf(card.data) }

    fun update() {
      editorVM.onSubmit {
        updateCardVM.updateCard(card.id, it)
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
