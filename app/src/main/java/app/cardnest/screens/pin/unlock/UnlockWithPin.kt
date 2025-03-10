package app.cardnest.screens.pin.unlock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.pin.Keypad
import app.cardnest.components.pin.PinInput
import app.cardnest.screens.pin.unlock.help.UnlockWithPinHelpScreen
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_RED
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.utils.extensions.collectValue
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.lifecycle.LifecycleEffectOnce
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UnlockWithPinScreen : Screen {
  @OptIn(ExperimentalVoyagerApi::class)
  @Composable
  override fun Content() {
    val ctx = LocalContext.current as FragmentActivity
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<UnlockWithPinViewModel> { parametersOf(navigator) }

    val hasEnabledBiometrics = vm.hasEnabledBiometrics.collectValue()

    fun onUnlockWithBiometrics() {
      vm.unlockWithBiometrics(ctx)
    }

    fun onHelp() {
      navigator.push(UnlockWithPinHelpScreen())
    }

    LifecycleEffectOnce {
      if (vm.getShouldUnlockWithBiometrics(ctx)) {
        onUnlockWithBiometrics()
      }
    }

    SubScreenRoot(title = "", actionLabel = "Help", onAction = ::onHelp) {
      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Unlock CardNest",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        if (hasEnabledBiometrics) {
          AppText("Unlock using your biometrics or PIN.", align = TextAlign.Center)
        } else {
          AppText("Unlock using your PIN.", align = TextAlign.Center)
        }
      }

      Spacer(Modifier.size(32.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        PinInput(
          pin = vm.pin,
          hasError = vm.hasError,
          isLoading = if (vm.hasError) false else vm.hasMaxLength
        )

        AppText(
          text = if (vm.showErrorMessage) "Entered PIN is incorrect" else "",
          modifier = Modifier.padding(top = 24.dp),
          size = AppTextSize.SM,
          color = TH_RED
        )
      }

      Spacer(Modifier.weight(1f))

      Keypad(
        onKeyClick = vm::onKeyClick,
        onBackspaceClick = vm::onBackspaceClick,
        isDisabled = vm.hasMaxLength,
        showBiometricsIcon = hasEnabledBiometrics,
        onBiometricsIconClick = ::onUnlockWithBiometrics
      )

      Spacer(Modifier.size(48.dp))
    }
  }
}
