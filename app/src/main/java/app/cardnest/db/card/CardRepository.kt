package app.cardnest.db.card

import androidx.datastore.core.DataStore
import app.cardnest.data.card.CardData
import app.cardnest.data.card.CardRecords
import app.cardnest.data.preferencesState
import app.cardnest.data.userState
import app.cardnest.firebase.realtime_db.CardDbManager
import app.cardnest.utils.extensions.checkNotNull
import kotlinx.coroutines.flow.Flow

class CardRepository(private val db: DataStore<CardRecords>, private val remoteDb: CardDbManager) {
  private val uid get() = userState.value?.uid.checkNotNull { "User must be signed in to perform auth operations" }
  private val isSyncing get() = preferencesState.value.sync.isSyncing

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
      throw IllegalArgumentException("Unencrypted card records cannot be saved in remote database")
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
          is CardData.Unencrypted -> throw IllegalArgumentException("Unencrypted card can't be saved in encrypted records")
        }

        is CardRecords.Unencrypted -> when (card) {
          is CardData.Unencrypted -> it.copy(it.cards.put(card.unencrypted.id, card.unencrypted))
          is CardData.Encrypted -> throw IllegalArgumentException("Encrypted card can't be saved in unencrypted records")
        }
      }
    }
  }

  suspend fun addOrUpdateRemoteCard(card: CardData) {
    if (card is CardData.Encrypted) {
      remoteDb.addOrUpdateCard(uid, card.encrypted)
    } else {
      throw IllegalArgumentException("Unencrypted card cannot be saved in remote database")
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
