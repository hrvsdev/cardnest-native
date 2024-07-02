package com.hrvs.cardnest.screens.home.card.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hrvs.cardnest.components.button.AppButton
import com.hrvs.cardnest.components.card.CardEditor
import com.hrvs.cardnest.components.containers.SubScreenRoot
import com.hrvs.cardnest.data.serializables.CardRecord
import com.hrvs.cardnest.screens.home.HomeScreen
import com.hrvs.cardnest.screens.home.card.CardViewScreen
import com.hrvs.cardnest.state.card.CardEditorViewModel
import com.hrvs.cardnest.state.card.updateCard
import kotlinx.coroutines.launch

data class UpdateCardEditorScreen(val cardRecord: CardRecord) : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow

    val viewModel = remember { CardEditorViewModel(cardRecord.plainData) }

    val scope = rememberCoroutineScope()

    fun update() {
      viewModel.onSubmit {
        scope.launch {
          val updatedCard = CardRecord(cardRecord.id, it)

          if (updatedCard == cardRecord) {
            navigator.pop()
            return@launch
          }

          updateCard(ctx, updatedCard)
          navigator.replaceAll(listOf(HomeScreen(), CardViewScreen(updatedCard)))
        }
      }
    }

    SubScreenRoot(
      title = "Edit Card",
      rightButtonLabel = "Done",
      onRightButtonClick = ::update,
      spacedBy = 32.dp
    ) {
      CardEditor(viewModel)
      AppButton(title = "Update", onClick = ::update)
    }
  }
}
