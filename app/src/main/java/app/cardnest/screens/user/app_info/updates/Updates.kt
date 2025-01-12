package app.cardnest.screens.user.app_info.updates

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.components.settings.SettingsSwitch
import app.cardnest.utils.extensions.collectValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UpdatesScreen : Screen {
  @Composable
  override fun Content() {
    val uriHandler = LocalUriHandler.current
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<UpdatesViewModel> { parametersOf(bottomSheetNavigator) }

    val checkAtLaunch = vm.checkAtLaunch.collectValue()

    val label = when (val result = vm.updatesState) {
      is UpdatesState.Idle -> "Check for updates now"
      is UpdatesState.Checking -> "Checking for updates"
      is UpdatesState.NoUpdate -> "You are up to date"
      is UpdatesState.UpdateAvailable -> "Update to CardNest ${result.version}"
    }

    fun showUpdateSheet(result: UpdatesState.UpdateAvailable) {
      bottomSheetNavigator.show(UpdatesBottomSheetScreen(result.version, result.downloadUrl))
    }

    fun onUpdate() {
      when (val result = vm.updatesState) {
        is UpdatesState.Checking -> return
        is UpdatesState.Idle, UpdatesState.NoUpdate -> vm.checkForUpdates()
        is UpdatesState.UpdateAvailable -> showUpdateSheet(result)
      }
    }

    LaunchedEffect(vm.updatesState) {
      val result = vm.updatesState
      if (result is UpdatesState.UpdateAvailable) showUpdateSheet(result)
    }

    SubScreenRoot("Updates", backLabel = "Settings", spacedBy = 24.dp) {
      SettingsGroup("App Updates") {
        SettingsButton(
          title = label,
          icon = painterResource(R.drawable.tabler__refresh_dot),
          isFirst = true,
          isLoading = vm.updatesState is UpdatesState.Checking,
          onClick = ::onUpdate
        )
        SettingsSwitch(
          title = "Check at launch",
          icon = painterResource(R.drawable.tabler__progress_check),
          isLast = true,
          checked = checkAtLaunch,
          onCheckedChange = vm::toggleCheckAtLaunch
        )
      }
    }
  }
}
