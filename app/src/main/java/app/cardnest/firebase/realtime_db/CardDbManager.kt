package app.cardnest.firebase.realtime_db

import android.util.Log
import app.cardnest.data.card.CardData
import app.cardnest.data.card.CardDataWithId
import app.cardnest.data.card.CardEncrypted
import app.cardnest.data.card.CardEncryptedEncodedWithIdForDb
import app.cardnest.data.card.CardEncryptedEncodedWithIdForDbNullable
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

  fun getCards(uid: String): Flow<CardRecords> = callbackFlow {
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

  suspend fun setCards(uid: String, cards: CardRecords) {
    val ref = db.getReference("$uid/cards")
    val cardsEncodedForDb: MutableMap<String, CardEncryptedEncodedWithIdForDb> = mutableMapOf()

    for (it in cards.cards) {
      val cardData = it.value.data
      if (cardData is CardData.Encrypted) {
        val cardEncryptedEncoded = CardEncryptedEncodedWithIdForDb(
          id = it.key,
          cipherText = cardData.card.cipherText.toEncoded(),
          iv = cardData.card.iv.toEncoded(),
          modifiedAt = it.value.modifiedAt
        )

        cardsEncodedForDb[it.key] = cardEncryptedEncoded
      }
    }

    try {
      ref.setValue(cardsEncodedForDb).await()
    } catch (e: Exception) {
      Log.e("RealtimeDbManager", "Failed to save data", e)
    }
  }

  suspend fun addOrUpdateCard(uid: String, card: CardDataWithId) {
    val ref = db.getReference("$uid/cards/${card.id}")
    val cardEncodedForDb = when (card.data) {
      is CardData.Unencrypted -> return
      is CardData.Encrypted -> CardEncryptedEncodedWithIdForDb(
        id = card.id,
        cipherText = card.data.card.cipherText.toEncoded(),
        iv = card.data.card.iv.toEncoded(),
        modifiedAt = card.modifiedAt
      )
    }

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

  private fun getCardRecordsFromSnapshot(snapshot: DataSnapshot): CardRecords {
    val cards: MutableMap<String, CardDataWithId> = mutableMapOf()

    for (child in snapshot.children) {
      val data = child.getValue(CardEncryptedEncodedWithIdForDbNullable::class.java)
      if (data == null || data.id == null || data.cipherText == null || data.iv == null || data.modifiedAt == null) continue

      val cardData = CardData.Encrypted(CardEncrypted(data.cipherText.toDecoded(), data.iv.toDecoded()))
      cards[data.id] = CardDataWithId(data.id, cardData, data.modifiedAt)
    }

    return CardRecords(cards.toPersistentMap())
  }
}
