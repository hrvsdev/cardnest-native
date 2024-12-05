package app.cardnest.screens.user.security

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

class RemovePinBottomSheetScreen(private val hasCreatedPassword: Boolean, private val hasEnabledBiometrics: Boolean) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Remove PIN")

      BottomSheetDescription("Are you sure you want to remove your PIN?")

      if (hasCreatedPassword) {
        BottomSheetDescription(buildAnnotatedString {
          append("If you remove the PIN, you will need to ")
          appendWithEmphasis("use your biometrics or password ")
          append("to unlock the app.")
        })

      } else {
        if (hasEnabledBiometrics) {
          BottomSheetDescription(buildAnnotatedString {
            append("Removing the PIN will ")
            appendWithEmphasis("disable biometrics ")
            append("too.")
          })
        }

        BottomSheetDescription(buildAnnotatedString {
          append("It will ")
          appendWithEmphasis("reduce security ")
          append("and make your data accessible to anyone with your device.")
        })
      }

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Confirm", ButtonTheme.Danger)
      }
    }
  }
}
