package app.cardnest.firebase.realtime_db

import android.util.Log
import app.cardnest.data.card.CardEncrypted
import app.cardnest.data.card.CardEncryptedData
import app.cardnest.data.card.CardEncryptedNullable
import app.cardnest.data.card.CardRecords
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class CardDbManager {
  val db = Firebase.database("https://cardnest-app-default-rtdb.asia-southeast1.firebasedatabase.app/")

  fun getCards(uid: String): Flow<CardRecords.Encrypted> = callbackFlow {
    val ref = db.getReference("$uid/cards")

    val listener = ref.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        trySend(getCardRecordsFromSnapshot(snapshot))
      }

      override fun onCancelled(error: DatabaseError) {
        Log.e("RealtimeDbManager", "Failed to read value.", error.toException())
      }
    })

    awaitClose { ref.removeEventListener(listener) }
  }

  suspend fun setCards(uid: String, cards: CardRecords.Encrypted) {
    val ref = db.getReference("$uid/cards")

    try {
      ref.setValue(cards).await()
    } catch (e: Exception) {
      Log.e("RealtimeDbManager", "Failed to save data", e)
    }
  }

  suspend fun addOrUpdateCard(uid: String, card: CardEncrypted) {
    val ref = db.getReference("$uid/cards/${card.id}")

    try {
      ref.setValue(card).await()
    } catch (e: Exception) {
      Log.e("RealtimeDbManager", "Failed to save data", e)
    }
  }

  suspend fun deleteCard(uid: String, cardId: String) {
    val ref = db.getReference("$uid/cards/$cardId")
    try {
      ref.removeValue().await()
    } catch (e: Exception) {
      Log.e("RealtimeDbManager", "Failed to delete data", e)
    }
  }

  private fun getCardRecordsFromSnapshot(snapshot: DataSnapshot): CardRecords.Encrypted {
    val cards: MutableMap<String, CardEncrypted> = mutableMapOf()

    for (child in snapshot.children) {
      val data = child.getValue(CardEncryptedNullable::class.java)
      if (data == null || data.id == null || data.data == null || data.modifiedAt == null) continue
      if (data.data.cipherText == null || data.data.iv == null) continue

      cards[data.id] = CardEncrypted(data.id, CardEncryptedData(data.data.cipherText, data.data.iv), data.modifiedAt)
    }

    return CardRecords.Encrypted(cards.toPersistentMap())
  }
}
