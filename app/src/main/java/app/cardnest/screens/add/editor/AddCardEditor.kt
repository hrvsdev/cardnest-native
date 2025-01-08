package app.cardnest.screens.add.editor

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import app.cardnest.components.button.AppButton
import app.cardnest.components.card.CardEditor
import app.cardnest.components.containers.SubScreenContainer
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.header.HeaderActionButton
import app.cardnest.components.header.SubScreenHeader
import app.cardnest.data.card.CardEditorViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class AddCardEditorScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val addCardVM = koinViewModel<AddCardViewModel> { parametersOf(navigator) }
    val editorVM = koinViewModel<CardEditorViewModel>()

    fun onSave() {
      editorVM.onSubmit {
        addCardVM.addCard(it)
      }
    }

    SubScreenRoot {
      SubScreenHeader("New Card") {
        HeaderActionButton(label = "Done", onClick = ::onSave)
      }

      SubScreenContainer(32.dp) {
        CardEditor(editorVM)
        AppButton(title = "Save", onClick = ::onSave)
      }
    }
  }
}
