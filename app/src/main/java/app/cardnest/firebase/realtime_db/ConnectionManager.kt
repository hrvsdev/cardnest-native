package app.cardnest.firebase.realtime_db

import android.util.Log
import app.cardnest.firebase.rtDb
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.callbackFlow

class ConnectionManager {
  init {
    rtDb.setPersistenceEnabled(true)
  }

  fun getConnectionState() = callbackFlow {
    val ref = rtDb.getReference(".info/connected")
    ref.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        trySend(snapshot.value == true)
      }

      override fun onCancelled(error: DatabaseError) {
        Log.e("RealtimeDbManager", "Failed to read value.", error.toException())
      }
    })
  }
}
