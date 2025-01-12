package app.cardnest

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen
import app.cardnest.utils.extensions.collectValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppRoot(splashScreen: SplashScreen) {
  MaterialTheme {
    val vm = koinViewModel<AppViewModel>()

    val initialScreen = vm.initialScreen
    val availableUpdate = vm.availableUpdate.collectValue()
    val isPasswordStale = vm.isPasswordStale.collectValue()

    val isLoading = initialScreen == null

    splashScreen.setKeepOnScreenCondition { isLoading }

    if (isLoading.not()) {
      App(initialScreen, isPasswordStale, availableUpdate)
    }
  }
}
