package com.hrvs.cardnest.screens.home.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.hrvs.cardnest.R
import com.hrvs.cardnest.components.button.AppButton
import com.hrvs.cardnest.components.button.ButtonTheme
import com.hrvs.cardnest.components.card.CardPreview
import com.hrvs.cardnest.components.containers.SubScreenRoot
import com.hrvs.cardnest.components.core.CopyableTextField
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.screens.home.card.editor.UpdateCardEditorScreen
import com.hrvs.cardnest.utils.card.addCardNumberSpaces

data class CardViewScreen(val card: CardFullProfile) : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    fun edit() {
      navigator.push(UpdateCardEditorScreen(card))
    }

    fun del() {
      // TODO
    }

    fun showDeleteBottomSheet() {
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
      onRightButtonClick = { edit() },
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

      AppButton(title = "Delete", onClick = { showDeleteBottomSheet() }, ButtonTheme.Danger)
    }
  }
}
