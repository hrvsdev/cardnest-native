package app.cardnest.screens.user

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.button.ButtonVariant

data class DeleteDataBottomSheetScreen(val onConfirm: () -> Unit, val onClose: () -> Unit) :
  Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Delete all data")

      BottomSheetDescription("Are you sure you want to delete all your data from all your devices?")
      BottomSheetDescription("This action cannot be undone.")

      BottomSheetButtons {
        AppButton("Cancel", onClose, variant = ButtonVariant.Flat)
        AppButton("Confirm", onConfirm, theme = ButtonTheme.Danger)
      }
    }
  }
}
