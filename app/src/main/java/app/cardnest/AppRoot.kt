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

    val isLoading = vm.hasAuthLoaded.collectAsStateWithLifecycle().value.not()
    val hasCreatedPin = vm.hasCreatedPin.collectAsStateWithLifecycle().value

    splashScreen.setKeepOnScreenCondition { isLoading }

    if (!isLoading) {
      App(hasCreatedPin)
    }
  }
}
