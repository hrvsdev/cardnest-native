package app.cardnest.db

import androidx.datastore.core.DataStore
import app.cardnest.data.serializables.CardRecord
import app.cardnest.data.serializables.CardRecords
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class CardDataOperations(private val ds: DataStore<CardRecords>) {
  fun getCards(): Flow<CardRecords> {
    return ds.data
  }

  fun getCard(id: String): Flow<CardRecord?> {
    return ds.data.mapLatest { it.cards[id] }
  }

  suspend fun addCard(card: CardRecord) {
    ds.updateData { it.copy(it.cards.put(card.id, card)) }
  }

  suspend fun updateCard(card: CardRecord) {
    ds.updateData { it.copy(it.cards.put(card.id, card)) }
  }

  suspend fun deleteCard(id: String) {
    ds.updateData { it.copy(it.cards.remove(id)) }
  }
}
