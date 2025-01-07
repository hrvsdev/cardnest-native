package app.cardnest.screens.home.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.card.CardPreview
import app.cardnest.components.containers.SubScreenContainer
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.core.CopyableTextField
import app.cardnest.components.header.HeaderActionButton
import app.cardnest.components.header.SubScreenHeader
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.screens.home.card.editor.UpdateCardEditorScreen
import app.cardnest.utils.card.addCardNumberSpaces
import app.cardnest.utils.extensions.open
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

data class CardViewScreen(val cardWithMeta: CardUnencrypted) : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val viewModel = koinViewModel<CardViewModel> { parametersOf(navigator) }
    val card = cardWithMeta.data

    fun del() {
      bottomSheetNavigator.hide()
      viewModel.deleteCard(cardWithMeta.id)
    }

    fun onEditClick() {
      navigator.push(UpdateCardEditorScreen(cardWithMeta))
    }

    fun onDeleteClick() {
      bottomSheetNavigator.open(DeleteCardBottomSheetScreen(), ::del)
    }

    SubScreenRoot {
      SubScreenHeader("Card") {
        HeaderActionButton(
          label = "Edit",
          icon = painterResource(id = R.drawable.tabler__pencil),
          isLoading = false,
          onClick = ::onEditClick
        )
      }

      SubScreenContainer(32.dp) {
        CardPreview(card)
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
          CopyableTextField(label = "Card number", text = addCardNumberSpaces(card.number), textToCopy = card.number)

          CopyableTextField(label = "Expiry date", text = card.expiry)
          CopyableTextField(label = "Cardholder name", text = card.cardholder)

          if (card.cvv.isNotBlank()) {
            CopyableTextField(label = "CVV", text = card.cvv)
          }

          if (card.issuer.isNotBlank()) {
            CopyableTextField(label = "Issuer", text = card.issuer)
          }
        }

        AppButton(title = "Delete", onClick = ::onDeleteClick, theme = ButtonTheme.Danger, isLoading = viewModel.isDeleting)
      }
    }
  }
}
