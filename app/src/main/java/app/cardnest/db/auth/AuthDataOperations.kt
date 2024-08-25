package app.cardnest.db.auth

import androidx.datastore.core.DataStore
import app.cardnest.data.auth.AuthData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

@OptIn(ExperimentalCoroutinesApi::class)
class AuthDataOperations(private val ds: DataStore<AuthData>) {
  fun getAuthData(): Flow<AuthData> {
    return ds.data
  }

  suspend fun setAuthData(authData: AuthData) {
    ds.updateData { authData }
  }
}
