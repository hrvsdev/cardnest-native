package app.cardnest.screens.user.app_info.updates

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.preferencesState
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.launchWithIO
import app.cardnest.utils.extensions.stateInViewModel
import app.cardnest.utils.extensions.toastAndLog
import app.cardnest.utils.updates.UpdatesManager
import app.cardnest.utils.updates.UpdatesResult
import kotlinx.coroutines.flow.map

class UpdatesViewModel(private val prefsManager: PreferencesManager, private val updatesManager: UpdatesManager) : ViewModel() {
  val checkAtLaunch = preferencesState.map { it.updates.checkAtLaunch }.stateInViewModel(true)

  var updatesState by mutableStateOf<UpdatesState>(UpdatesState.Idle)
    private set

  fun checkForUpdates() {
    updatesState = UpdatesState.Checking
    launchWithIO {
      try {
        val result = updatesManager.checkForUpdates()
        updatesState = when (result) {
          is UpdatesResult.NoUpdate -> UpdatesState.NoUpdate
          is UpdatesResult.UpdateAvailable -> UpdatesState.UpdateAvailable(result.version, result.downloadUrl)
        }

      } catch (e: Exception) {
        e.toastAndLog("UpdatesViewModel")
        updatesState = UpdatesState.Idle
      }
    }
  }

  fun toggleCheckAtLaunch(checked: Boolean) {
    launchDefault {
      prefsManager.setCheckUpdatesAtLaunch(checked)
    }
  }
}


