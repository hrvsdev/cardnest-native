package app.cardnest.screens.password.sign_in.help

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import cafe.adriel.voyager.core.screen.Screen

class ForgotPasswordBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Forgot password")

      BottomSheetDescription("It is never stored, so it can't be recovered.")
      BottomSheetDescription("However, you can create a new account to start fresh.")

      BottomSheetButtons {
        BottomSheetPrimaryButton("Okay")
      }
    }
  }
}
