package app.cardnest.db.card

import androidx.datastore.core.DataStore
import app.cardnest.data.card.CardDataWithId
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

  fun getRemoteCards(): Flow<CardRecords> {
    return remoteDb.getCards(uid)
  }

  suspend fun setCards(cards: CardRecords) {
    if (isSyncing) setRemoteCards(cards) else setLocalCards(cards)
  }

  suspend fun setLocalCards(cards: CardRecords) {
    db.updateData { cards }
  }

  suspend fun setRemoteCards(cards: CardRecords) {
    remoteDb.setCards(uid, cards)
  }

  suspend fun addOrUpdateCard(card: CardDataWithId) {
    if (isSyncing) addOrUpdateRemoteCard(card) else addOrUpdateLocalCard(card)
  }

  suspend fun addOrUpdateLocalCard(card: CardDataWithId) {
    db.updateData { it.copy(it.cards.put(card.id, card)) }
  }

  suspend fun addOrUpdateRemoteCard(card: CardDataWithId) {
    remoteDb.addOrUpdateCard(uid, card)
  }

  suspend fun deleteCard(id: String) {
    if (isSyncing) deleteRemoteCard(id) else deleteLocalCard(id)
  }

  suspend fun deleteLocalCard(id: String) {
    db.updateData { it.copy(it.cards.remove(id)) }
  }

  suspend fun deleteRemoteCard(id: String) {
    remoteDb.deleteCard(uid, id)
  }
}
