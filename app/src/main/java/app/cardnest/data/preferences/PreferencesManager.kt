package app.cardnest.data.preferences

import app.cardnest.db.preferences.PreferencesRepository

class PreferencesManager(private val repo: PreferencesRepository) {
  suspend fun setMaskCardNumber(maskCardNumber: Boolean) {
    repo.setPreferences { it.copy(userInterface = it.userInterface.copy(maskCardNumber)) }
  }

  suspend fun setSync(isSyncing: Boolean) {
    repo.setPreferences { it.copy(sync = it.sync.copy(isSyncing)) }
  }
}
