package app.cardnest.screens.user.security

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetCancelButton
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import app.cardnest.components.button.ButtonTheme
import cafe.adriel.voyager.core.screen.Screen

class RemovePinBottomSheetScreen : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Remove app PIN")

      BottomSheetDescription("Are you sure you want to remove the PIN?")
      BottomSheetDescription("Sync will be turned off. Anyone who has access to your device will be able to see your data.")

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Confirm", ButtonTheme.Danger)
      }
    }
  }
}
