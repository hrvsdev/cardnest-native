package app.cardnest.db.auth

import androidx.datastore.core.DataStore
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.BiometricsData
import app.cardnest.data.auth.EncryptedDataEncoded
import app.cardnest.data.auth.PasswordData
import app.cardnest.data.auth.PinData
import app.cardnest.data.auth.RemoteAuthData
import app.cardnest.data.auth.RemoteAuthDataNullable
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

class AuthRepository(private val localDb: DataStore<AuthData>) {
  private val uid get() = initialUserState.value?.uid.checkNotNull { "User must be signed in to perform auth operations" }

  fun getLocalAuthData(): Flow<AuthData> {
    try {
      return localDb.data
    } catch (e: Exception) {
      throw Exception("Error getting auth record", e)
    }
  }

  fun getRemoteAuthData(): Flow<RemoteAuthData?> = callbackFlow {
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

  suspend fun setLocalPasswordData(data: PasswordData?) {
    localDb.updateData { it.copy(password = data) }
  }

  suspend fun setLocalPinData(data: PinData?) {
    localDb.updateData { it.copy(pin = data) }
  }

  suspend fun setLocalBiometricsData(data: BiometricsData?) {
    localDb.updateData { it.copy(biometrics = data) }
  }

  suspend fun setRemotePasswordData(data: PasswordData) {
    val ref = rtDb.getReference("$uid/auth/password")
    val data = PasswordData(data.salt, data.encryptedDek, modifiedAt = data.modifiedAt)
    try {
      ref.setValue(data).await()
    } catch (e: DatabaseException) {
      throw Exception("Error saving auth data", e)
    }
  }

  suspend fun removeRemotePasswordData() {
    val ref = rtDb.getReference("$uid/auth/password")
    try {
      ref.removeValue().await()
    } catch (e: DatabaseException) {
      throw Exception("Error removing auth data", e)
    }
  }

  private fun getAuthDataFromSnapshot(snapshot: DataSnapshot): RemoteAuthData? {
    val data = snapshot.getValue(RemoteAuthDataNullable::class.java) ?: return null

    if (data.password == null) {
      throw IllegalStateException("Auth data seems to be corrupted")
    }

    if (data.password.salt == null || data.password.encryptedDek == null || data.password.modifiedAt == null) {
      throw IllegalStateException("Password data seems to be corrupted")
    }

    if (data.password.encryptedDek.ciphertext == null || data.password.encryptedDek.iv == null) {
      throw IllegalStateException("Encrypted encryption key seems to be corrupted")
    }

    val passwordData = PasswordData(
      salt = data.password.salt,
      encryptedDek = EncryptedDataEncoded(data.password.encryptedDek.ciphertext, data.password.encryptedDek.iv),
      modifiedAt = data.password.modifiedAt,
    )

    return RemoteAuthData(passwordData)
  }
}
