package app.cardnest.screens.user.account

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.button.ButtonVariant
import cafe.adriel.voyager.core.screen.Screen

data class CreatePinBottomSheetScreen(val onConfirm: () -> Unit, val onCancel: () -> Unit) :
  Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Create a PIN")

      BottomSheetDescription("Creating a PIN is mandatory before signing in.")
      BottomSheetDescription("This is to ensure that your data is secure between server and devices.")

      BottomSheetButtons {
        AppButton("Cancel", onCancel, variant = ButtonVariant.Flat)
        AppButton("Continue", onConfirm, theme = ButtonTheme.Primary)
      }
    }
  }
}
