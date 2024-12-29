package app.cardnest.screens.pin.verify.help

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading

import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import cafe.adriel.voyager.core.screen.Screen

data class ForgotPinBottomSheetScreen(private val hasCreatedPassword: Boolean) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Forgot PIN")

      BottomSheetDescription("It is never stored, so it can't be recovered.")

      if (hasCreatedPassword) {
        BottomSheetDescription("However, you can sign-out and sign-in again to create a new PIN.")
      } else {
        BottomSheetDescription("However, you can re-install the app to create a new PIN, but this will delete all your data.")
      }

      BottomSheetButtons {
        BottomSheetPrimaryButton("Okay")
      }
    }
  }
}
