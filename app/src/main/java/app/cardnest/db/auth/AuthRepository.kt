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
import app.cardnest.utils.extensions.withIO
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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
    withIO {
      try {
        localDb.updateData { it.copy(password = data) }
      } catch (e: Exception) {
        throw Exception("Error setting local password data", e)
      }
    }
  }

  suspend fun setLocalPinData(data: PinData?) {
    withIO {
      try {
        localDb.updateData { it.copy(pin = data) }
      } catch (e: Exception) {
        throw Exception("Error setting local pin data", e)
      }
    }
  }

  suspend fun setLocalBiometricsData(data: BiometricsData?) {
    withIO {
      try {
        localDb.updateData { it.copy(biometrics = data) }
      } catch (e: Exception) {
        throw Exception("Error setting local biometrics data", e)
      }
    }
  }

  suspend fun setRemotePasswordData(data: PasswordData) {
    withIO {
      try {
        val ref = rtDb.getReference("$uid/auth/password")
        ref.setValue(data)
      } catch (e: Exception) {
        throw Exception("Error setting remote password data", e)
      }
    }
  }

  suspend fun removeRemotePasswordData() {
    withIO {
      try {
        val ref = rtDb.getReference("$uid/auth/password")
        ref.removeValue()
      } catch (e: Exception) {
        throw Exception("Error removing remote password data", e)
      }
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
