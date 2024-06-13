package com.hrvs.cardnest.screens.home.card

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.bottomSheet.BottomSheet
import com.hrvs.cardnest.components.bottomSheet.BottomSheetButtons
import com.hrvs.cardnest.components.bottomSheet.BottomSheetDescription
import com.hrvs.cardnest.components.bottomSheet.BottomSheetHeading
import com.hrvs.cardnest.components.button.AppButton
import com.hrvs.cardnest.components.button.ButtonTheme
import com.hrvs.cardnest.components.button.ButtonVariant

data class DeleteCardBottomSheetScreen(
  val onConfirm: () -> Unit,
  val onClose: () -> Unit
) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Delete card")

      BottomSheetDescription("Are you sure you want to delete this card?")
      BottomSheetDescription("This action cannot be undone.")

      BottomSheetButtons {
        AppButton("Cancel", onClose, variant = ButtonVariant.Flat)
        AppButton("Delete", onConfirm, theme = ButtonTheme.Danger)
      }
    }
  }
}
