package app.cardnest.screens.pin.create.create

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetCancelButton
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import cafe.adriel.voyager.core.screen.Screen

class CreatePinBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Create a PIN")

      BottomSheetDescription("Creating a PIN is mandatory to sync data.")
      BottomSheetDescription("This is to ensure that your data is secure between server and devices.")

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Continue")
      }
    }
  }
}
