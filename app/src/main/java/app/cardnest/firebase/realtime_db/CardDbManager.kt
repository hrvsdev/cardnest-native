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

  fun setCards(cards: CardRecords, uid: String) {
    val ref = db.getReference("$uid/cards")
    val cardsEncodedForDb: MutableMap<String, CardEncryptedEncodedWithIdForDb> = mutableMapOf()

    for (it in cards.cards) {
      val cardData = it.value.data
      if (cardData is CardData.Encrypted) {
        val cardEncryptedEncoded = CardEncryptedEncodedWithIdForDb(
          id = it.key,
          cipherText = cardData.card.cipherText.toEncoded(),
          iv = cardData.card.iv.toEncoded()
        )

        cardsEncodedForDb[it.key] = cardEncryptedEncoded
      }
    }

    ref.setValue(cardsEncodedForDb).addOnCompleteListener {
      if (!it.isSuccessful) {
        Log.e("RealtimeDbManager", "Failed to save data", it.exception)
      }
    }
  }

  fun addOrUpdateCard(card: CardEncryptedEncodedWithIdForDb, uid: String) {
    val ref = db.getReference("$uid/cards/${card.id}")
    ref.setValue(card).addOnCompleteListener {
      if (!it.isSuccessful) {
        Log.e("RealtimeDbManager", "Failed to save data", it.exception)
      }
    }
  }

  fun deleteCard(cardId: String, uid: String) {
    val ref = db.getReference("$uid/cards/$cardId")
    ref.removeValue().addOnCompleteListener {
      if (!it.isSuccessful) {
        Log.e("RealtimeDbManager", "Failed to delete data", it.exception)
      }
    }
  }

  private fun getCardRecordsFromSnapshot(snapshot: DataSnapshot): CardRecords {
    val cards: MutableMap<String, CardDataWithId> = mutableMapOf()

    for (child in snapshot.children) {
      val data = child.getValue(CardEncryptedEncodedWithIdForDbNullable::class.java)
      if (data == null || data.id == null || data.cipherText == null || data.iv == null) continue

      val cardData = CardData.Encrypted(CardEncrypted(data.cipherText.toDecoded(), data.iv.toDecoded()))
      cards[data.id] = CardDataWithId(data.id, cardData)
    }

    return CardRecords(cards.toPersistentMap())
  }
}
