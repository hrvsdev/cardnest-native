package app.cardnest.screens.pin.verify_new_pin

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetCancelButton
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import cafe.adriel.voyager.core.screen.Screen

class ProvideNewPinBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Provide new PIN")

      BottomSheetDescription("You PIN has changed from another device.")
      BottomSheetDescription("You need to provide the same to decrypt the data.")

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Continue")
      }
    }
  }
}
