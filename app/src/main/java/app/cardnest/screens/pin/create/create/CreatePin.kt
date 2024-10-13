package app.cardnest.screens.pin.create.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.button.AppButton
import app.cardnest.components.button.ButtonTheme
import app.cardnest.components.button.ButtonVariant
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.pin.Keypad
import app.cardnest.components.pin.PinInput
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_WHITE
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

const val PIN_LENGTH = 6

class CreatePinScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val vm = koinViewModel<CreatePinViewModel> { parametersOf(navigator) }

    SubScreenRoot(title = "") {
      Spacer(Modifier.weight(1f))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Create a PIN",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Please enter a secure 6-digit PIN.")
        AppText("Remember it as there is no way to recover it.", Modifier.padding(bottom = 32.dp))

        PinInput(
          pin = vm.pin.value,
          hasError = vm.hasError.value,
          isLoading = if (vm.hasError.value) false else vm.pin.value.length == PIN_LENGTH
        )

        AppText(
          text = if (vm.showErrorMessage.value) "Entered PIN is too common" else "",
          modifier = Modifier.padding(top = 24.dp),
          size = AppTextSize.SM,
          color = TH_RED
        )
      }

      Spacer(Modifier.weight(2f))
      Keypad(vm.pin, vm::onPinChange, vm::onPinSubmit)
      Spacer(Modifier.weight(1f))
    }
  }
}

data class CreatePinBottomSheetScreen(val onConfirm: () -> Unit, val onCancel: () -> Unit) : Screen {
  @Composable
  override fun Content() {
    BottomSheet {
      BottomSheetHeading("Create a PIN")

      BottomSheetDescription("Creating a PIN is mandatory to sync data.")
      BottomSheetDescription("This is to ensure that your data is secure between server and devices.")

      BottomSheetButtons {
        AppButton("Cancel", onCancel, variant = ButtonVariant.Flat)
        AppButton("Continue", onConfirm, theme = ButtonTheme.Primary)
      }
    }
  }
}
