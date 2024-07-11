package app.cardnest.screens.home.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import app.cardnest.LocalCardsDataVM
import app.cardnest.R
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.card.CardPreview
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.core.CopyableTextField
import app.cardnest.data.serializables.CardRecord
import app.cardnest.screens.home.card.editor.UpdateCardEditorScreen
import app.cardnest.utils.card.addCardNumberSpaces
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CardViewScreen(val cardRecord: CardRecord) : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current
    val cardsDataVM = LocalCardsDataVM.current

    val scope = rememberCoroutineScope()
    val card = cardRecord.plainData

    fun del() {
      bottomSheetNavigator.hide()
      cardsDataVM.deleteCard(cardRecord.id)

      scope.launch {
        delay(200)
        navigator.pop()
      }
    }

    fun onEditClick() {
      navigator.push(UpdateCardEditorScreen(cardRecord))
    }

    fun onDeleteClick() {
      bottomSheetNavigator.show(
        DeleteCardBottomSheetScreen(
          onConfirm = { del() },
          onClose = { bottomSheetNavigator.hide() }
        )
      )
    }

    SubScreenRoot(
      title = "Card",
      rightButtonLabel = "Edit",
      rightButtonIcon = painterResource(R.drawable.tabler__pencil),
      onRightButtonClick = ::onEditClick,
      spacedBy = 32.dp,
    ) {

      CardPreview(card)
      Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
        CopyableTextField(
          label = "Card number",
          text = addCardNumberSpaces(card.number),
          textToCopy = card.number,
        )

        CopyableTextField(label = "Expiry date", text = card.expiry)
        CopyableTextField(label = "Cardholder name", text = card.cardholder)

        if (card.issuer.isNotBlank()) {
          CopyableTextField(label = "Issuer", text = card.issuer)
        }
      }

      AppButton(title = "Delete", onClick = ::onDeleteClick, ButtonTheme.Danger)
    }
  }
}
