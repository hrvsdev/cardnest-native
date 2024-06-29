package com.hrvs.cardnest.screens.add.editor

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import com.hrvs.cardnest.HomeTab
import com.hrvs.cardnest.components.button.AppButton
import com.hrvs.cardnest.components.card.CardEditor
import com.hrvs.cardnest.components.containers.SubScreenRoot
import com.hrvs.cardnest.data.serializables.CardRecord
import com.hrvs.cardnest.state.card.CardEditorViewModel
import com.hrvs.cardnest.state.card.addCard
import com.hrvs.cardnest.utils.genId
import kotlinx.coroutines.launch

class AddCardEditorScreen : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    val tabNavigator = LocalTabNavigator.current
    val viewModel = remember { CardEditorViewModel() }

    val scope = rememberCoroutineScope()

    fun saveCard() {
      viewModel.onSubmit {
        scope.launch {
          val id = genId()
          val card = CardRecord(id, it)

          addCard(ctx, card)

          tabNavigator.current = HomeTab
          navigator.popUntilRoot()
        }
      }
    }

    SubScreenRoot(
      title = "New Card",
      rightButtonLabel = "Done",
      onRightButtonClick = ::saveCard,
      spacedBy = 32.dp
    ) {
      CardEditor(viewModel)
      AppButton(title = "Save", onClick = ::saveCard)
    }
  }
}
