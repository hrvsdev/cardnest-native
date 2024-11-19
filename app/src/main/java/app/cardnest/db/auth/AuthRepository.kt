package app.cardnest.db.auth

import androidx.datastore.core.DataStore
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthDataRemote
import app.cardnest.data.auth.AuthDataRemoteNullable
import app.cardnest.data.auth.AuthRecord
import app.cardnest.data.auth.BiometricsData
import app.cardnest.data.auth.EncryptedDataEncoded
import app.cardnest.data.auth.PinData
import app.cardnest.data.initialUserState
import app.cardnest.firebase.rtDb
import app.cardnest.utils.extensions.checkNotNull
import app.cardnest.utils.extensions.toastAndLog
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthRepository(private val localDb: DataStore<AuthRecord>) {
  private val uid get() = initialUserState.value?.uid.checkNotNull { "User must be signed in to perform auth operations" }

  fun getLocalAuthRecord(): Flow<AuthRecord> {
    try {
      return localDb.data
    } catch (e: Exception) {
      throw Exception("Error getting auth record", e)
    }
  }

  fun getRemoteAuthData(): Flow<AuthData?> = callbackFlow {
    val ref = rtDb.getReference("$uid/auth")
    val listener = ref.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        try {
          trySend(getAuthDataFromSnapshot(snapshot))
        } catch (e: Exception) {
          trySend(null)
          e.toastAndLog("AuthRepository")
        }
      }

      override fun onCancelled(error: DatabaseError) {
        close(Exception("Error getting remote auth data", error.toException()))
      }
    })

    awaitClose { ref.removeEventListener(listener) }
  }

  suspend fun setAuthData(authData: AuthData) {
    setLocalAuthData(authData)
    setRemoteAuthData(authData)
  }

  suspend fun setLocalAuthData(authData: AuthData?) {
    localDb.updateData { it.copy(authData) }
  }

  suspend fun setRemoteAuthData(authData: AuthData) {
    val ref = rtDb.getReference("$uid/auth")
    val remoteAuthData = AuthDataRemote(authData.salt, authData.encryptedDek, modifiedAt = authData.modifiedAt)
    try {
      ref.setValue(remoteAuthData).await()
    } catch (e: DatabaseException) {
      throw Exception("Error saving auth data", e)
    }
  }

  suspend fun setLocalPinData(data: PinData?) {
    localDb.updateData { it.copy(pin = data) }
  }

  suspend fun setLocalBiometricsData(data: BiometricsData?) {
    localDb.updateData { it.copy(biometrics = data) }
  }

  private fun getAuthDataFromSnapshot(snapshot: DataSnapshot): AuthData? {
    val data = snapshot.getValue(AuthDataRemoteNullable::class.java) ?: return null

    if (data.salt == null || data.encryptedDek == null || data.modifiedAt == null) {
      throw IllegalStateException("Auth data seems to be corrupted")
    }

    if (data.encryptedDek.ciphertext == null || data.encryptedDek.iv == null) {
      throw IllegalStateException("Encrypted encryption key seems to be corrupted")
    }

    return AuthData(
      salt = data.salt,
      encryptedDek = EncryptedDataEncoded(data.encryptedDek.ciphertext, data.encryptedDek.iv),
      modifiedAt = data.modifiedAt,
    )
  }
}
