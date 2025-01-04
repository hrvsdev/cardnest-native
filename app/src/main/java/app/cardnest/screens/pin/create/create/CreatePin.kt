package app.cardnest.screens.pin.create.create

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
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
      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Create a PIN",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("It will be used to unlock the app.", align = TextAlign.Center)
        AppText(
          align = TextAlign.Center,
          text = buildAnnotatedString {
            append("It never gets stored, so it ")
            withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append("can't be recovered") }
            append(".")
          }
        )
      }

      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        PinInput(
          pin = vm.pin,
          hasError = vm.hasError,
          isLoading = if (vm.hasError) false else vm.hasMaxLength
        )

        AppText(
          text = if (vm.showErrorMessage) "Entered PIN is too common" else "",
          modifier = Modifier.padding(top = 24.dp),
          size = AppTextSize.SM,
          color = TH_RED
        )
      }

      Spacer(Modifier.weight(1f))
      Keypad(vm::onKeyClick, vm::onBackspaceClick, vm.hasMaxLength)

      Spacer(Modifier.size(48.dp))
    }
  }
}
