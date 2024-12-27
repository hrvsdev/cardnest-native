package app.cardnest.screens.user

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

class DeleteDataBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Delete all cards")

      BottomSheetDescription("Are you sure you want to delete all your cards?")
      BottomSheetDescription(buildAnnotatedString {
        append("Your all ")
        appendWithEmphasis("cards data will be permanently deleted ")
        append("from all your devices.")
      })

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Confirm", ButtonTheme.Danger)
      }
    }
  }
}
