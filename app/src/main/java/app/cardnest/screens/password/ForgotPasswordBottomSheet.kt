package app.cardnest.screens.password

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import cafe.adriel.voyager.core.screen.Screen

data class ForgotPasswordBottomSheetScreen(private val context: ForgotPasswordContext) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Forgot password")

      BottomSheetDescription("It is never stored, so it can't be recovered.")
      BottomSheetDescription(desc)

      BottomSheetButtons {
        BottomSheetPrimaryButton("Okay")
      }
    }
  }

  val desc = when (context) {
    ForgotPasswordContext.SIGN_IN -> "You can try signing in again, or start fresh by creating a new account."
    ForgotPasswordContext.UNLOCK -> "You can try unlocking with your biometrics or PIN, or sign-out and start fresh by creating a new account."
    ForgotPasswordContext.VERIFICATION, ForgotPasswordContext.CHANGE -> "However, you can start fresh by deleting this account and creating a new one."
  }
}

enum class ForgotPasswordContext {
  SIGN_IN,
  UNLOCK,
  VERIFICATION,
  CHANGE
}

