package app.cardnest.firebase.realtime_db

import android.util.Log
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
        Log.e("RealtimeDbManager", "Failed to read value", error.toException())
      }
    })

    awaitClose { ref.removeEventListener(listener) }
  }

  suspend fun setAuthData(uid: String, authData: AuthData) {
    val ref = db.getReference("$uid/auth")

    val salt = checkNotNull(authData.salt) { "Salt is required" }
    val encryptedDek = checkNotNull(authData.encryptedDek) { "Encrypted DEK is required" }
    val modifiedAt = checkNotNull(authData.modifiedAt) { "ModifiedAt is required" }

    try {
      ref.setValue(AuthDataRemote(salt, encryptedDek, true, modifiedAt)).await()
    } catch (e: DatabaseException) {
      Log.e("RealtimeDbManager", "Failed to save data", e)
    }
  }

  private fun getAuthDataFromSnapshot(snapshot: DataSnapshot): AuthData? {
    val data = snapshot.getValue(AuthDataRemoteNullable::class.java) ?: return null
    if (data.salt == null || data.encryptedDek == null || data.modifiedAt == null) error("Salt, encrypted DEK and modifiedAt are required")
    if (data.encryptedDek.ciphertext == null || data.encryptedDek.iv == null) error("Encrypted DEK, ciphertext and IV are required")

    return AuthData(
      salt = data.salt,
      encryptedDek = EncryptedDataEncoded(data.encryptedDek.ciphertext, data.encryptedDek.iv),
      modifiedAt = data.modifiedAt,
      encryptedBiometricsDek = authData.value?.encryptedBiometricsDek,
    )
  }
}
