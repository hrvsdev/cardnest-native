package app.cardnest

import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen
import app.cardnest.ui.theme.CardNestTheme
import app.cardnest.utils.extensions.collectValue
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppRoot(splashScreen: SplashScreen) {
  CardNestTheme {
    val vm = koinViewModel<AppViewModel>()

    val initialScreen = vm.initialScreen
    val isPasswordStale = vm.isPasswordStale.collectValue()

    val isLoading = initialScreen == null

    splashScreen.setKeepOnScreenCondition { isLoading }

    if (isLoading.not()) {
      App(initialScreen, isPasswordStale)
    }
  }
}
