package app.cardnest.firebase.realtime_db

import android.util.Log
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthDataRemote
import app.cardnest.data.auth.AuthDataRemoteNullable
import app.cardnest.data.auth.EncryptedDataEncoded
import app.cardnest.data.authData
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class AuthDbManager {
  private val db = Firebase.database("https://cardnest-app-default-rtdb.asia-southeast1.firebasedatabase.app/")

  suspend fun getAuthData(uid: String): AuthData? {
    val ref = db.getReference("$uid/auth")

    try {
      val snapshot = ref.get().await()
      val data = snapshot.getValue(AuthDataRemoteNullable::class.java) ?: return null

      if (data.salt == null || data.encryptedDek == null) return null
      if (data.encryptedDek.ciphertext == null || data.encryptedDek.iv == null) return null

      return AuthData(
        salt = data.salt,
        encryptedDek = EncryptedDataEncoded(data.encryptedDek.ciphertext, data.encryptedDek.iv),
        hasCreatedPin = data.hasCreatedPin,

        encryptedBiometricsDek = authData.value.encryptedBiometricsDek,
        hasBiometricsEnabled = authData.value.hasBiometricsEnabled
      )

    } catch (e: DatabaseException) {
      Log.e("RealtimeDbManager", "Failed to read value.", e)
      return null
    }
  }

  suspend fun setAuthData(authData: AuthData, uid: String) {
    val ref = db.getReference("$uid/auth")

    val salt = checkNotNull(authData.salt) { "Salt is required" }
    val encryptedDek = checkNotNull(authData.encryptedDek) { "Encrypted DEK is required" }

    try {
      ref.setValue(AuthDataRemote(salt, encryptedDek)).await()
    } catch (e: DatabaseException) {
      Log.e("RealtimeDbManager", "Failed to save data", e)
    }
  }
}
