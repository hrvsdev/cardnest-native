package app.cardnest.db

import app.cardnest.data.serializables.CardRecord

class CardRepository(private val dataOperations: CardDataOperations) {
  fun getCards() = dataOperations.getCards()
  fun getCard(id: String) = dataOperations.getCard(id)
  suspend fun addCard(card: CardRecord) = dataOperations.addCard(card)
  suspend fun updateCard(card: CardRecord) = dataOperations.updateCard(card)
  suspend fun deleteCard(id: String) = dataOperations.deleteCard(id)
}
