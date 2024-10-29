package app.cardnest.firebase.realtime_db

import app.cardnest.data.card.CardEncrypted
import app.cardnest.data.card.CardEncryptedData
import app.cardnest.data.card.CardEncryptedNullable
import app.cardnest.data.card.CardRecords
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
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
        throw Exception("Error getting cards", error.toException())
      }
    })

    awaitClose { ref.removeEventListener(listener) }
  }

  suspend fun setCards(uid: String, cards: CardRecords.Encrypted) {
    val ref = db.getReference("$uid/cards")

    try {
      ref.setValue(cards.cards).await()
    } catch (e: DatabaseException) {
      throw Exception("Error saving cards", e)
    }
  }

  suspend fun addOrUpdateCard(uid: String, card: CardEncrypted) {
    val ref = db.getReference("$uid/cards/${card.id}")

    try {
      ref.setValue(card).await()
    } catch (e: DatabaseException) {
      throw Exception("Error saving card", e)
    }
  }

  suspend fun deleteCard(uid: String, cardId: String) {
    val ref = db.getReference("$uid/cards/$cardId")
    try {
      ref.removeValue().await()
    } catch (e: DatabaseException) {
      throw Exception("Error deleting card", e)
    }
  }

  private fun getCardRecordsFromSnapshot(snapshot: DataSnapshot): CardRecords.Encrypted {
    val cards: MutableMap<String, CardEncrypted> = mutableMapOf()

    for (child in snapshot.children) {
      val data = child.getValue(CardEncryptedNullable::class.java)

      if (data == null || data.id == null || data.data == null || data.modifiedAt == null) {
        throw IllegalStateException("Card data or metadata seems to be corrupted")
      }

      if (data.data.cipherText == null || data.data.iv == null) {
        throw IllegalStateException("Encrypted card data seems to be corrupted")
      }

      cards[data.id] = CardEncrypted(data.id, CardEncryptedData(data.data.cipherText, data.data.iv), data.modifiedAt)
    }

    return CardRecords.Encrypted(cards.toPersistentMap())
  }
}
