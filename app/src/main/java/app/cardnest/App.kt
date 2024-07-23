package app.cardnest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.components.containers.appGradient
import app.cardnest.components.tabs.TabBar
import app.cardnest.components.tabs.tabs
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.enter.EnterPinScreen
import app.cardnest.state.auth.AuthDataViewModel
import app.cardnest.ui.theme.TH_BLACK_00
import app.cardnest.ui.theme.TH_BLACK_60
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.koin.androidx.compose.koinViewModel

@Composable
fun App() {
  val authVM = koinViewModel<AuthDataViewModel>()

  val state = authVM.uiState.collectAsStateWithLifecycle().value
  val showPinScreen = state.hasCreatedPin

  LaunchedEffect(state) {
    println("State: $state")
  }

  BottomSheetNavigator(scrimColor = TH_BLACK_60, sheetBackgroundColor = TH_BLACK_00) {
    Navigator(if (showPinScreen) EnterPinScreen() else HomeScreen) {
      val showTabBar = it.lastItem in tabs.map { tab -> tab.screen }

      Box(Modifier.background(appGradient)) {
        SlideTransition(it)
        TabBar(showTabBar)
      }
    }
  }
}
