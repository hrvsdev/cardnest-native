package com.hrvs.cardnest.screens.home.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
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
import com.hrvs.cardnest.data.serializables.CardRecord
import com.hrvs.cardnest.screens.home.card.editor.UpdateCardEditorScreen
import com.hrvs.cardnest.state.card.deleteCard
import com.hrvs.cardnest.utils.card.addCardNumberSpaces
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class CardViewScreen(val cardRecord: CardRecord) : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val scope = rememberCoroutineScope()
    val card = cardRecord.plainData

    fun del() {
      scope.launch {
        bottomSheetNavigator.hide()
        delay(200)
        deleteCard(ctx, cardRecord.id)
        navigator.pop()
      }
    }

    fun onEditClick() {
      navigator.push(UpdateCardEditorScreen(card))
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
