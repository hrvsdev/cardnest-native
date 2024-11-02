package app.cardnest.data.preferences

import app.cardnest.data.preferencesState
import app.cardnest.db.preferences.PreferencesRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

class PreferencesManager(private val repo: PreferencesRepository) {
  suspend fun collectPreferences() {
    repo.getPreferences().collectLatest { d -> preferencesState.update { d } }
  }

  suspend fun setMaskCardNumber(maskCardNumber: Boolean) {
    repo.setPreferences { it.copy(userInterface = it.userInterface.copy(maskCardNumber)) }
  }

  suspend fun setSync(isSyncing: Boolean) {
    repo.setPreferences { it.copy(sync = it.sync.copy(isSyncing)) }
  }
}
