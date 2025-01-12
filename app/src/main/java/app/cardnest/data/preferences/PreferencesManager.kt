package app.cardnest.data.preferences

import app.cardnest.data.appDataState
import app.cardnest.data.preferencesState
import app.cardnest.db.preferences.PreferencesRepository
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

class PreferencesManager(private val repo: PreferencesRepository) {
  suspend fun collectPreferences() {
    repo.getPreferences().catch { it.toastAndLog("PreferencesManager") }.collectLatest { d ->
      preferencesState.update { d }
      appDataState.update { it.copy(prefs = true) }
    }
  }

  suspend fun setMaskCardNumber(maskCardNumber: Boolean) {
    repo.setPreferences { it.copy(userInterface = it.userInterface.copy(maskCardNumber)) }
  }

  suspend fun setCheckUpdatesAtLaunch(checkUpdatesAtLaunch: Boolean) {
    repo.setPreferences { it.copy(updates = it.updates.copy(checkAtLaunch = checkUpdatesAtLaunch)) }
  }
}
