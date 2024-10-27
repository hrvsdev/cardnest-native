package app.cardnest.firebase.realtime_db

import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthDataRemote
import app.cardnest.data.auth.AuthDataRemoteNullable
import app.cardnest.data.auth.EncryptedDataEncoded
import app.cardnest.data.authData
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthDbManager {
  private val db = Firebase.database("https://cardnest-app-default-rtdb.asia-southeast1.firebasedatabase.app/")

  fun getAuthData(uid: String) = callbackFlow {
    val ref = db.getReference("$uid/auth")
    val listener = ref.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        trySend(getAuthDataFromSnapshot(snapshot))
      }

      override fun onCancelled(error: DatabaseError) {
        throw Exception("Error getting auth data", error.toException())
      }
    })

    awaitClose { ref.removeEventListener(listener) }
  }

  suspend fun setAuthData(uid: String, authData: AuthData) {
    val ref = db.getReference("$uid/auth")
    val remoteAuthData = AuthDataRemote(
      salt = authData.salt,
      encryptedDek = authData.encryptedDek,
      modifiedAt = authData.modifiedAt,
    )

    try {
      ref.setValue(remoteAuthData).await()
    } catch (e: DatabaseException) {
      throw Exception("Error saving auth data", e)
    }
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
      encryptedBiometricsDek = authData.value?.encryptedBiometricsDek,
    )
  }
}
