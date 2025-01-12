package app.cardnest.screens.user.app_info.updates

import androidx.lifecycle.ViewModel
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.preferencesState
import app.cardnest.data.updatesState
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.launchWithIO
import app.cardnest.utils.extensions.stateInViewModel
import app.cardnest.utils.extensions.toastAndLog
import app.cardnest.utils.updates.UpdatesManager
import app.cardnest.utils.updates.UpdatesResult
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class UpdatesViewModel(private val prefsManager: PreferencesManager, private val updatesManager: UpdatesManager) : ViewModel() {
  val state = updatesState.stateInViewModel(UpdatesState.Idle)
  val checkAtLaunch = preferencesState.map { it.updates.checkAtLaunch }.stateInViewModel(true)

  fun checkForUpdates() {
    updatesState.update { UpdatesState.Checking }
    launchWithIO {
      try {
        val result = updatesManager.checkForUpdates()
        val state = when (result) {
          is UpdatesResult.NoUpdate -> UpdatesState.NoUpdate
          is UpdatesResult.UpdateAvailable -> UpdatesState.UpdateAvailable(result.version, result.downloadUrl)
        }

        updatesState.update { state }
      } catch (e: Exception) {
        e.toastAndLog("UpdatesViewModel")
        updatesState.update { UpdatesState.Idle }
      }
    }
  }

  fun toggleCheckAtLaunch(checked: Boolean) {
    launchDefault {
      prefsManager.setCheckUpdatesAtLaunch(checked)
    }
  }
}


