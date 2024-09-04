package app.cardnest.db.preferences

import app.cardnest.data.preferences.Preferences

class PreferencesRepository(private val dataOperations: PreferencesDataOperations) {
  fun getPreferences() = dataOperations.getPreferences()
  suspend fun setPreferences(transform: (Preferences) -> Preferences) = dataOperations.setPreferences(transform)
}
