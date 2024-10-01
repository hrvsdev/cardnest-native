package app.cardnest.firebase.realtime_db

import android.util.Log
import app.cardnest.data.card.CardEncrypted
import app.cardnest.data.card.CardEncryptedData
import app.cardnest.data.card.CardEncryptedDataEncodedForDb
import app.cardnest.data.card.CardEncryptedEncodedForDb
import app.cardnest.data.card.CardEncryptedEncodedForDbNullable
import app.cardnest.data.card.CardRecords
import app.cardnest.utils.extensions.toDecoded
import app.cardnest.utils.extensions.toEncoded
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
    val cardsEncodedForDb = cards.cards.mapValues {
      CardEncryptedEncodedForDb(
        id = it.value.id,
        data = CardEncryptedDataEncodedForDb(it.value.data.cipherText.toEncoded(), it.value.data.iv.toEncoded()),
        modifiedAt = it.value.modifiedAt
      )
    }

    try {
      ref.setValue(cardsEncodedForDb).await()
    } catch (e: Exception) {
      Log.e("RealtimeDbManager", "Failed to save data", e)
    }
  }

  suspend fun addOrUpdateCard(uid: String, card: CardEncrypted) {
    val ref = db.getReference("$uid/cards/${card.id}")
    val cardEncodedForDb = CardEncryptedEncodedForDb(
      id = card.id,
      data = CardEncryptedDataEncodedForDb(card.data.cipherText.toEncoded(), card.data.iv.toEncoded()),
      modifiedAt = card.modifiedAt
    )

    try {
      ref.setValue(cardEncodedForDb).await()
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
      val data = child.getValue(CardEncryptedEncodedForDbNullable::class.java)
      if (data == null || data.id == null || data.data == null || data.modifiedAt == null) continue
      if (data.data.cipherText == null || data.data.iv == null) continue

      val card = CardEncrypted(
        id = data.id,
        data = CardEncryptedData(data.data.cipherText.toDecoded(), data.data.iv.toDecoded()),
        modifiedAt = data.modifiedAt
      )

      cards[data.id] = card
    }

    return CardRecords.Encrypted(cards.toPersistentMap())
  }
}
