package app.cardnest.screens.pin.verify_new_pin

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.button.ButtonVariant
import cafe.adriel.voyager.core.screen.Screen

data class ProvideNewPinBottomSheetScreen(val onConfirm: () -> Unit, val onCancel: () -> Unit) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Provide new PIN")

      BottomSheetDescription("You PIN has changed from another device.")
      BottomSheetDescription("You need to provide the same to decrypt the data.")

      BottomSheetButtons {
        AppButton("Cancel", onCancel, variant = ButtonVariant.Flat)
        AppButton("Continue", onConfirm, theme = ButtonTheme.Primary)
      }
    }
  }
}
