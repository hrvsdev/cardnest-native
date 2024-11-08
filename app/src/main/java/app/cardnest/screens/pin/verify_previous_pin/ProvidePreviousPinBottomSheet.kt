package app.cardnest.screens.pin.verify_previous_pin

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetCancelButton
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import cafe.adriel.voyager.core.screen.Screen

class ProvidePreviousPinBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Provide previous PIN")

      BottomSheetDescription("You need to provide your previous PIN to decrypt the data.")
      BottomSheetDescription("Canceling will keep your data encrypted on server and sync won't be enabled.")

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Continue")
      }
    }
  }
}
