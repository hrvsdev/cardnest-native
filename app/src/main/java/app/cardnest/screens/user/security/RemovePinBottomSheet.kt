package app.cardnest.screens.user.security

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.button.ButtonVariant

data class RemovePinBottomSheetScreen(val onConfirm: () -> Unit, val onClose: () -> Unit) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Remove app PIN")

      BottomSheetDescription("Are you sure you want to remove the PIN?")
      BottomSheetDescription("Anyone who has access to your device will be able to see your data.")

      BottomSheetButtons {
        AppButton("Cancel", onClose, variant = ButtonVariant.Flat)
        AppButton("Confirm", onConfirm, theme = ButtonTheme.Danger)
      }
    }
  }
}
