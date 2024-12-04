package app.cardnest.screens.user.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.buildAnnotatedString
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetCancelButton
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import app.cardnest.utils.extensions.appendWithEmphasis
import cafe.adriel.voyager.core.screen.Screen

class EnableBiometricsBottomSheetScreen() : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Enable biometrics")

      BottomSheetDescription("You need a backup method to enable biometrics.")
      BottomSheetDescription(buildAnnotatedString {
        append("Please ")
        appendWithEmphasis("create a PIN ")
        append("or ")
        appendWithEmphasis("sign-in using password ")
        append("first.")
      })

      BottomSheetDescription("If biometrics system or hardware fails, your PIN or password can be used.")

      BottomSheetButtons {
        BottomSheetCancelButton()
        BottomSheetPrimaryButton("Create PIN")
      }
    }
  }
}
