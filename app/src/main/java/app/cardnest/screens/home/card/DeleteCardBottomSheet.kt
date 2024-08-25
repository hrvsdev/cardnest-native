package app.cardnest.screens.home.card

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.button.ButtonVariant
import cafe.adriel.voyager.core.screen.Screen

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
