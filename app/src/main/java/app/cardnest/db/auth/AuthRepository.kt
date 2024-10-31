package app.cardnest.db.auth

import androidx.datastore.core.DataStore
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthRecord
import app.cardnest.data.userState
import app.cardnest.firebase.realtime_db.AuthDbManager
import app.cardnest.utils.extensions.checkNotNull
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthRepository(private val db: DataStore<AuthRecord>, private val remoteDb: AuthDbManager) {
  private val uid get() = userState.value?.uid.checkNotNull { "User must be signed in to perform auth operations" }
  private val isSyncing get() = userState.value?.isSyncing == true

  fun getLocalAuthData(): Flow<AuthData?> {
    return db.data.map { it.data }
  }

  fun getRemoteAuthData(): Flow<AuthData?> {
    return remoteDb.getAuthData(uid)
  }

  suspend fun setAuthData(authData: AuthData) {
    setLocalAuthData(authData)
    if (isSyncing) setRemoteAuthData(authData)
  }

  suspend fun setLocalAuthData(authData: AuthData?) {
    db.updateData { it.copy(authData) }
  }

  suspend fun setRemoteAuthData(authData: AuthData) {
    remoteDb.setAuthData(uid, authData)
  }
}
