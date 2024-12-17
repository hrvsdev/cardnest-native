package app.cardnest.db.card

import androidx.datastore.core.DataStore
import app.cardnest.data.card.CardData
import app.cardnest.data.card.CardEncrypted
import app.cardnest.data.card.CardEncryptedData
import app.cardnest.data.card.CardEncryptedNullable
import app.cardnest.data.card.CardRecords
import app.cardnest.data.userState
import app.cardnest.firebase.rtDb
import app.cardnest.utils.extensions.checkNotNull
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class CardRepository(private val localDb: DataStore<CardRecords>) {
  private val uid get() = userState.value?.uid.checkNotNull { "User must be signed in to perform card operations" }
  private val isSyncing get() = userState.value?.uid != null

  fun getLocalCards(): Flow<CardRecords> {
    try {
      return localDb.data
    } catch (e: Exception) {
      throw Exception("Error getting cards", e)
    }
  }

  fun getRemoteCards(): Flow<CardRecords.Encrypted> = callbackFlow {
    val ref = rtDb.getReference("$uid/cards")
    val listener = ref.addValueEventListener(object : ValueEventListener {
      override fun onDataChange(snapshot: DataSnapshot) {
        try {
          trySend(getCardRecordsFromSnapshot(snapshot))
        } catch (e: Exception) {
          close(Exception(e))
        }
      }

      override fun onCancelled(error: DatabaseError) {
        close(Exception("Error getting remote cards", error.toException()))
      }
    })

    awaitClose { ref.removeEventListener(listener) }
  }

  suspend fun setCards(cards: CardRecords) {
    if (isSyncing) {
      setRemoteCards(cards)
    } else {
      setLocalCards(cards)
    }
  }

  suspend fun setLocalCards(cards: CardRecords) {
    localDb.updateData { cards }
  }

  fun setRemoteCards(cards: CardRecords) {
    if (cards is CardRecords.Encrypted) {
      try {
        val ref = rtDb.getReference("$uid/cards")
        ref.setValue(cards.cards)
      } catch (e: Exception) {
        throw Exception("Error setting remote cards", e)
      }
    } else {
      throw IllegalArgumentException("Unencrypted card records cannot be saved in remote database")
    }
  }

  suspend fun addOrUpdateCard(card: CardData) {
    if (isSyncing) {
      addOrUpdateRemoteCard(card)
    } else {
      addOrUpdateLocalCard(card)
    }
  }

  suspend fun addOrUpdateLocalCard(card: CardData) {
    localDb.updateData {
      when (it) {
        is CardRecords.Encrypted -> when (card) {
          is CardData.Encrypted -> it.copy(it.cards.put(card.encrypted.id, card.encrypted))
          is CardData.Unencrypted -> throw IllegalArgumentException("Unencrypted card can't be saved in encrypted records")
        }

        is CardRecords.Unencrypted -> when (card) {
          is CardData.Unencrypted -> it.copy(it.cards.put(card.unencrypted.id, card.unencrypted))
          is CardData.Encrypted -> throw IllegalArgumentException("Encrypted card can't be saved in unencrypted records")
        }
      }
    }
  }

  fun addOrUpdateRemoteCard(card: CardData) {
    if (card is CardData.Encrypted) {
      try {
        val ref = rtDb.getReference("$uid/cards/${card.encrypted.id}")
        ref.setValue(card.encrypted)
      } catch (e: Exception) {
        throw Exception("Error adding or updating remote card", e)
      }
    } else {
      throw IllegalArgumentException("Unencrypted card can't be saved in remote database")
    }
  }

  suspend fun deleteCard(id: String) {
    if (isSyncing) {
      deleteRemoteCard(id)
    } else {
      deleteLocalCard(id)
    }
  }

  suspend fun deleteLocalCard(id: String) {
    localDb.updateData {
      when (it) {
        is CardRecords.Encrypted -> it.copy(it.cards.remove(id))
        is CardRecords.Unencrypted -> it.copy(it.cards.remove(id))
      }
    }
  }

  fun deleteRemoteCard(id: String) {
    try {
      val ref = rtDb.getReference("$uid/cards/$id")
      ref.removeValue()
    } catch (e: Exception) {
      throw Exception("Error deleting remote card", e)
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
