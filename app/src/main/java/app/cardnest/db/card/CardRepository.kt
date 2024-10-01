package app.cardnest.db.card

import androidx.datastore.core.DataStore
import app.cardnest.data.card.CardData
import app.cardnest.data.card.CardRecords
import app.cardnest.data.userState
import app.cardnest.firebase.realtime_db.CardDbManager
import kotlinx.coroutines.flow.Flow

class CardRepository(private val db: DataStore<CardRecords>, private val remoteDb: CardDbManager) {
  private val uid get() = checkNotNull(userState.value?.uid) { "User is not logged in" }
  private val isSyncing get() = userState.value?.isSyncing == true

  fun getLocalCards(): Flow<CardRecords> {
    return db.data
  }

  fun getRemoteCards(): Flow<CardRecords.Encrypted> {
    return remoteDb.getCards(uid)
  }

  suspend fun setCards(cards: CardRecords) {
    if (isSyncing) setRemoteCards(cards) else setLocalCards(cards)
  }

  suspend fun setLocalCards(cards: CardRecords) {
    db.updateData { cards }
  }

  suspend fun setRemoteCards(cards: CardRecords) {
    if (cards is CardRecords.Encrypted) {
      remoteDb.setCards(uid, cards)
    } else {
      error("Unencrypted card records cannot be set to remote database")
    }
  }

  suspend fun addOrUpdateCard(card: CardData) {
    if (isSyncing) addOrUpdateRemoteCard(card) else addOrUpdateLocalCard(card)
  }

  suspend fun addOrUpdateLocalCard(card: CardData) {
    db.updateData {
      when (it) {
        is CardRecords.Encrypted -> when (card) {
          is CardData.Encrypted -> it.copy(it.cards.put(card.encrypted.id, card.encrypted))
          is CardData.Unencrypted -> error("Unencrypted card data cannot be added to encrypted records")
        }

        is CardRecords.Unencrypted -> when (card) {
          is CardData.Unencrypted -> it.copy(it.cards.put(card.unencrypted.id, card.unencrypted))
          is CardData.Encrypted -> error("Encrypted card data cannot be added to unencrypted records")
        }
      }
    }
  }

  suspend fun addOrUpdateRemoteCard(card: CardData) {
    if (card is CardData.Encrypted) {
      remoteDb.addOrUpdateCard(uid, card.encrypted)
    } else {
      error("Unencrypted card data cannot be added to remote database")
    }
  }

  suspend fun deleteCard(id: String) {
    if (isSyncing) deleteRemoteCard(id) else deleteLocalCard(id)
  }

  suspend fun deleteLocalCard(id: String) {
    db.updateData {
      when (it) {
        is CardRecords.Encrypted -> it.copy(it.cards.remove(id))
        is CardRecords.Unencrypted -> it.copy(it.cards.remove(id))
      }
    }
  }

  suspend fun deleteRemoteCard(id: String) {
    remoteDb.deleteCard(uid, id)
  }
}
