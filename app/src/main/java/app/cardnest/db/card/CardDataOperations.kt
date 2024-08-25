package app.cardnest.db.card

import androidx.datastore.core.DataStore
import app.cardnest.data.card.CardDataWithId
import app.cardnest.data.card.CardRecords
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapLatest

@OptIn(ExperimentalCoroutinesApi::class)
class CardDataOperations(private val ds: DataStore<CardRecords>) {
  fun getCards(): Flow<CardRecords> {
    return ds.data
  }

  suspend fun setCards(cards: CardRecords) {
    ds.updateData { cards }
  }

  fun getCard(id: String): Flow<CardDataWithId?> {
    return ds.data.mapLatest { it.cards[id] }
  }

  suspend fun addCard(card: CardDataWithId) {
    ds.updateData { it.copy(it.cards.put(card.id, card)) }
  }

  suspend fun updateCard(card: CardDataWithId) {
    ds.updateData { it.copy(it.cards.put(card.id, card)) }
  }

  suspend fun deleteCard(id: String) {
    ds.updateData { it.copy(it.cards.remove(id)) }
  }
}
