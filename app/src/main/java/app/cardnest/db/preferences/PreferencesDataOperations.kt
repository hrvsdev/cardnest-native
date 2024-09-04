package app.cardnest.db.preferences

import androidx.datastore.core.DataStore
import app.cardnest.data.preferences.Preferences
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
class PreferencesDataOperations(private val ds: DataStore<Preferences>) {
  fun getPreferences(): Flow<Preferences> {
    return ds.data
  }

  suspend fun setPreferences(transform: (Preferences) -> Preferences) {
    ds.updateData(transform)
  }
}
