package app.cardnest.firebase.realtime_db

import android.util.Log
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthDataForDbNullable
import app.cardnest.data.auth.toDecoded
import app.cardnest.data.auth.toEncoded
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
      val data = snapshot.getValue(AuthDataForDbNullable::class.java) ?: return null
      return data.toDecoded()

    } catch (e: DatabaseException) {
      Log.e("RealtimeDbManager", "Failed to read value.", e)
      return null
    }
  }

  suspend fun setAuthData(authData: AuthData, uid: String) {
    val ref = db.getReference("$uid/auth")

    try {
      ref.setValue(authData.toEncoded()).await()
    } catch (e: DatabaseException) {
      Log.e("RealtimeDbManager", "Failed to save data", e)
    }
  }
}
