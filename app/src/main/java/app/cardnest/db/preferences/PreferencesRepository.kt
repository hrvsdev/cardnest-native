package app.cardnest.db.preferences

import androidx.datastore.core.DataStore
import app.cardnest.data.preferences.Preferences
import kotlinx.coroutines.flow.Flow

class PreferencesRepository(private val localDb: DataStore<Preferences>) {
  fun getPreferences(): Flow<Preferences> {
    try {
      return localDb.data
    } catch (e: Exception) {
      throw Exception("Error getting preferences", e)
    }
  }

  suspend fun setPreferences(transform: (Preferences) -> Preferences) {
    localDb.updateData(transform)
  }
}
