package app.cardnest.screens.pin

import androidx.compose.runtime.Composable
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading

import app.cardnest.components.bottomSheet.BottomSheetPrimaryButton
import cafe.adriel.voyager.core.screen.Screen

data class ForgotPinBottomSheetScreen(private val context: ForgotPinContext, private val hasCreatedPassword: Boolean) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Forgot PIN")

      BottomSheetDescription("It is never stored, so it can't be recovered.")
      BottomSheetDescription(desc)

      BottomSheetButtons {
        BottomSheetPrimaryButton("Okay")
      }
    }
  }

  val desc = if (hasCreatedPassword) when (context) {
    ForgotPinContext.UNLOCK -> "You can always unlock with your password, or sign-out and sign-in again to create a new PIN."
    ForgotPinContext.VERIFICATION -> "However, you can sign-out and sign-in again to create a new PIN."
  } else when (context) {
    ForgotPinContext.UNLOCK, ForgotPinContext.VERIFICATION -> "However, you can start fresh by re-installing the app, but doing so will delete all your data."
  }
}

enum class ForgotPinContext {
  UNLOCK,
  VERIFICATION
}
