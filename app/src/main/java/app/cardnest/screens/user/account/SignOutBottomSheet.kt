package app.cardnest.screens.user.account

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetCancelButton
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import app.cardnest.components.button.ButtonTheme
import cafe.adriel.voyager.core.screen.Screen

class SignOutBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Sign out")

      BottomSheetDescription("Are you sure you want to sign-out?")
      BottomSheetDescription("Your data including password, PIN and biometrics will be removed from this device.")

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Confirm", ButtonTheme.Danger)
      }
    }
  }
}
