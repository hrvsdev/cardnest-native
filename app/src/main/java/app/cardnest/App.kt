package app.cardnest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import app.cardnest.components.containers.appGradient
import app.cardnest.components.tabs.TabBar
import app.cardnest.components.tabs.tabs
import app.cardnest.components.toast.AppToast
import app.cardnest.screens.password.unlock.UnlockWithNewPasswordScreen
import app.cardnest.screens.user.app_info.updates.UpdatesBottomSheetScreen
import app.cardnest.screens.user.app_info.updates.UpdatesState
import app.cardnest.ui.theme.TH_BLACK_00
import app.cardnest.ui.theme.TH_BLACK_60
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.transitions.SlideTransition

@Composable
fun App(initialScreen: Screen, isPasswordStale: Boolean, availableUpdate: UpdatesState.UpdateAvailable?) {
  BottomSheetNavigator(scrimColor = TH_BLACK_60, sheetBackgroundColor = TH_BLACK_00) {
    LaunchedEffect(availableUpdate) {
      if (availableUpdate != null) it.show(UpdatesBottomSheetScreen(availableUpdate.version, availableUpdate.downloadUrl))
    }

    Navigator(initialScreen) {
      val showTabBar = it.lastItem in tabs.map { tab -> tab.screen }

      LaunchedEffect(isPasswordStale) {
        if (isPasswordStale) it.replaceAll(UnlockWithNewPasswordScreen())
      }

      Box(Modifier.background(appGradient)) {
        SlideTransition(it)
        TabBar(showTabBar)
        AppToast()
      }
    }
  }
}
