package app.cardnest.firebase

import android.util.Log
import app.cardnest.data.Connection
import app.cardnest.data.connectionState
import app.cardnest.data.preferencesState
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update

class ConnectionManager {
  suspend fun collectConnectionState() {
    val firebaseConnectionFlow = callbackFlow {
      val ref = rtDb.getReference(".info/connected")
      val listener = ref.addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
          trySend(snapshot.value == true)
        }

        override fun onCancelled(error: DatabaseError) {
          Log.e("RealtimeDbManager", "Failed to read value.", error.toException())
        }
      })

      awaitClose { ref.removeEventListener(listener) }
    }

    val connectionStateFlow = firebaseConnectionFlow.combine(preferencesState) { isConnected, prefs ->
      Connection(shouldWrite = if (prefs.sync.isSyncing) isConnected else true, isConnected)
    }

    connectionStateFlow.collectLatest { d -> connectionState.update { d } }
  }
}
