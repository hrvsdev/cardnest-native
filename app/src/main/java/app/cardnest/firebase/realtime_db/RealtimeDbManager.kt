package app.cardnest.firebase.realtime_db

import android.util.Log
import app.cardnest.data.card.CardData
import app.cardnest.data.card.CardEncrypted
import app.cardnest.data.card.CardEncryptedEncodedWithIdForDb
import app.cardnest.data.card.CardEncryptedEncodedWithIdForDbNullable
import app.cardnest.data.userState
import app.cardnest.utils.extensions.toDecoded
import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class RealtimeDbManager {
  val db = Firebase.database("https://cardnest-app-default-rtdb.asia-southeast1.firebasedatabase.app/")

  fun collectCards(action: (Map<String, CardData.Encrypted>) -> Unit) {
    val uid = userState.value?.uid ?: return

    val ref = db.getReference("$uid/cards")
    ref.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        val cards: MutableMap<String, CardData.Encrypted> = mutableMapOf()

        for (child in snapshot.children) {
          val data = child.getValue(CardEncryptedEncodedWithIdForDbNullable::class.java)
          if (data == null || data.id == null || data.cipherText == null || data.iv == null) continue

          cards[data.id] = CardData.Encrypted(CardEncrypted(data.cipherText.toDecoded(), data.iv.toDecoded()))
        }

        action(cards)
      }

      override fun onCancelled(error: DatabaseError) {
        Log.e("RealtimeDbManager", "Failed to read value.", error.toException())
      }
    })
  }

  fun addOrUpdateCard(card: CardEncryptedEncodedWithIdForDb) {
    val uid = userState.value?.uid ?: return

    val ref = db.getReference("$uid/cards/${card.id}")
    ref.setValue(card).addOnCompleteListener {
      if (!it.isSuccessful) {
        Log.e("RealtimeDbManager", "Failed to save data", it.exception)
      }
    }
  }

  fun deleteCard(cardId: String) {
    val uid = userState.value?.uid ?: return

    val ref = db.getReference("$uid/cards/$cardId")
    ref.removeValue().addOnCompleteListener {
      if (!it.isSuccessful) {
        Log.e("RealtimeDbManager", "Failed to delete data", it.exception)
      }
    }
  }
}
