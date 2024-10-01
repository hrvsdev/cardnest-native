package app.cardnest.screens.home.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.R
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.card.CardPreview
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.core.CopyableTextField
import app.cardnest.screens.home.card.editor.UpdateCardEditorScreen
import app.cardnest.utils.card.addCardNumberSpaces
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class CardViewScreen(val id: String) : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val viewModel = koinViewModel<CardViewModel> { parametersOf(id, navigator) }
    val cardWithMeta = viewModel.cardWithMeta.collectAsStateWithLifecycle().value
    val card = cardWithMeta?.data?.copy()

    fun del() {
      bottomSheetNavigator.hide()
      viewModel.deleteCard()
    }

    fun onEditClick() {
      if (cardWithMeta != null) {
        navigator.push(UpdateCardEditorScreen(cardWithMeta))
      }
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

      if (card != null) {
        CardPreview(card)
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
          CopyableTextField(
            label = "Card number",
            text = addCardNumberSpaces(card.number),
            textToCopy = card.number,
          )

          CopyableTextField(label = "Expiry date", text = card.expiry)
          CopyableTextField(label = "Cardholder name", text = card.cardholder)

          if (card.cvv.isNotBlank()) {
            CopyableTextField(label = "CVV", text = card.cvv)
          }

          if (card.issuer.isNotBlank()) {
            CopyableTextField(label = "Issuer", text = card.issuer)
          }
        }

        AppButton(title = "Delete", onClick = ::onDeleteClick, ButtonTheme.Danger)
      }
    }
  }
}
