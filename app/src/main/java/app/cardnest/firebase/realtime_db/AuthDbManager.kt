package app.cardnest.firebase.realtime_db

import android.util.Log
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthDataForDb
import app.cardnest.data.auth.toDecoded
import app.cardnest.data.auth.toEncoded
import app.cardnest.data.userState
import com.google.firebase.Firebase
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.database
import kotlinx.coroutines.tasks.await

class AuthDbManager {
  private val db = Firebase.database("https://cardnest-app-default-rtdb.asia-southeast1.firebasedatabase.app/")

  suspend fun getAuthData(): AuthData {
    val uid = userState.value?.uid ?: return AuthData()
    val ref = db.getReference(uid).child("auth")

    try {
      val snapshot = ref.get().await()
      val data = snapshot.getValue(AuthDataForDb::class.java) ?: return AuthData()
      return data.toDecoded()

    } catch (e: DatabaseException) {
      Log.e("RealtimeDbManager", "Failed to read value.", e)
      return AuthData()
    }
  }

  suspend fun setAuthData(authData: AuthData) {
    val uid = userState.value?.uid ?: return
    val ref = db.getReference(uid).child("auth")

    try {
      ref.setValue(authData.toEncoded()).await()
    } catch (e: DatabaseException) {
      Log.e("RealtimeDbManager", "Failed to save data", e)
    }
  }
}
