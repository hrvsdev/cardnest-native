package app.cardnest.screens.user.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetCancelButton
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.utils.extensions.appendWithEmphasis
import cafe.adriel.voyager.core.screen.Screen

class DeleteAccountBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Delete account")

      BottomSheetDescription("Are you sure you want to delete your account?")
      BottomSheetDescription(buildAnnotatedString {
        append("Your all ")
        appendWithEmphasis("data will be permanently deleted")
        append(".")
      })

      BottomSheetDescription("You will have to sign in again to continue.")

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Continue", theme = ButtonTheme.Danger)
      }
    }
  }
}
