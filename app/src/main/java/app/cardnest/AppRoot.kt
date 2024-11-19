package app.cardnest

import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.ui.theme.CardNestTheme
import org.koin.androidx.compose.koinViewModel

@Composable
fun AppRoot(splashScreen: SplashScreen) {
  CardNestTheme {
    val vm = koinViewModel<AppViewModel>()

    val initialScreen = vm.initialScreen.collectAsStateWithLifecycle().value
    val isLoading = initialScreen == null

    splashScreen.setKeepOnScreenCondition { isLoading }

    if (isLoading.not()) {
      App(initialScreen)
    }
  }
}
