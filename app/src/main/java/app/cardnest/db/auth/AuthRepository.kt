package app.cardnest.db.auth

import androidx.datastore.core.DataStore
import app.cardnest.data.auth.AuthData
import app.cardnest.data.userState
import app.cardnest.firebase.realtime_db.AuthDbManager
import kotlinx.coroutines.flow.Flow

class AuthRepository(private val db: DataStore<AuthData>, private val remoteDb: AuthDbManager) {
  private val uid get() = checkNotNull(userState.value?.uid) { "User is not logged in" }
  private val isSyncing get() = userState.value?.isSyncing == true

  fun getLocalAuthData(): Flow<AuthData> {
    return db.data
  }

  fun getRemoteAuthData(): Flow<AuthData?> {
    return remoteDb.getAuthData(uid)
  }

  suspend fun setAuthData(authData: AuthData) {
    setLocalAuthData(authData)
    if (isSyncing) setRemoteAuthData(authData)
  }

  suspend fun setLocalAuthData(authData: AuthData) {
    db.updateData { authData }
  }

  suspend fun setRemoteAuthData(authData: AuthData) {
    remoteDb.setAuthData(uid, authData)
  }
}
