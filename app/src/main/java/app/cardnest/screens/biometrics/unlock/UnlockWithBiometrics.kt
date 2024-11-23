package app.cardnest.screens.biometrics.unlock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import app.cardnest.R
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.pin.KeypadIconButton
import app.cardnest.components.toast.AppToast
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.AppTextSize
import app.cardnest.ui.theme.TH_WHITE
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UnlockWithBiometricsScreen : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current as FragmentActivity
    val navigator = LocalNavigator.currentOrThrow

    val vm = koinViewModel<UnlockWithBiometricsViewModel> { parametersOf(navigator) }

    val canUnlockWithBiometrics = vm.getShowBiometricsButton(ctx)

    fun onUnlockWithBiometrics() {
      if (canUnlockWithBiometrics) {
        vm.unlockWithBiometrics(ctx)
      } else {
        AppToast.error("Biometrics are not available on your device at this time. Please try again later.")
      }
    }

    LaunchedEffect(canUnlockWithBiometrics) {
      onUnlockWithBiometrics()
    }

    ScreenContainer {
      Spacer(Modifier.size(64.dp))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        AppText(
          text = "Unlock CardNest",
          modifier = Modifier.padding(bottom = 8.dp),
          size = AppTextSize.XL,
          weight = FontWeight.Bold,
          color = TH_WHITE
        )

        AppText("Unlock using your biometrics", align = TextAlign.Center)
      }

      Spacer(Modifier.weight(1f))
      Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        KeypadIconButton(painterResource(R.drawable.heroicons__finger_print), ::onUnlockWithBiometrics)
      }

      Spacer(Modifier.size(48.dp))
    }
  }
}
