package app.cardnest.db.card

import app.cardnest.data.card.CardDataWithId
import app.cardnest.data.card.CardRecords

class CardRepository(private val dataOperations: CardDataOperations) {
  fun getCards() = dataOperations.getCards()
  suspend fun setCards(cards: CardRecords) = dataOperations.setCards(cards)

  fun getCard(id: String) = dataOperations.getCard(id)
  suspend fun addCard(card: CardDataWithId) = dataOperations.addCard(card)
  suspend fun updateCard(card: CardDataWithId) = dataOperations.updateCard(card)
  suspend fun deleteCard(id: String) = dataOperations.deleteCard(id)
}
