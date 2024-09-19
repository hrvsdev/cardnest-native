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

data class ProvidePreviousPinBottomSheetScreen(val onConfirm: () -> Unit, val onCancel: () -> Unit) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Provide previous PIN")

      BottomSheetDescription("You need to provide your previous PIN to decrypt the data.")
      BottomSheetDescription("Canceling will keep your data encrypted on server and sync won't be enabled.")

      BottomSheetButtons {
        AppButton("Cancel", onCancel, variant = ButtonVariant.Flat)
        AppButton("Continue", onConfirm, theme = ButtonTheme.Primary)
      }
    }
  }
}
