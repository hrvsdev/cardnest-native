package app.cardnest.screens.pin.unlock.help

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import cafe.adriel.voyager.core.screen.Screen

class ForgotPinBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Forgot PIN")

      BottomSheetDescription("It is never stored, so it can't be recovered.")
      BottomSheetDescription("However, you can unlock always unlock using your biometrics or password and set a new PIN.")

      BottomSheetButtons {
        BottomSheetPrimaryButton("Okay")
      }
    }
  }
}
