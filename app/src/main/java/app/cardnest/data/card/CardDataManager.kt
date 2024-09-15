package app.cardnest.data.card

import app.cardnest.data.auth.EncryptedData
import app.cardnest.data.authState
import app.cardnest.data.cardsState
import app.cardnest.data.userState
import app.cardnest.db.card.CardRepository
import app.cardnest.firebase.realtime_db.RealtimeDbManager
import app.cardnest.utils.crypto.CryptoManager
import app.cardnest.utils.extensions.toEncoded
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

class CardDataManager(
  private val realtimeDb: RealtimeDbManager,
  private val repo: CardRepository,
  private val crypto: CryptoManager
) {
  suspend fun decryptAndCollectCards() {
    if (userState.value != null) {
      realtimeDb.collectCards {
        val cardRecords = it.mapValues {
          CardRecord(it.key, decryptCardData(it.value))
        }

        cardsState.update { cardRecords }
      }

      return
    }

    repo.getCards().collectLatest {
      val cardRecords = it.cards.mapValues {
        CardRecord(it.key, decryptCardData(it.value.data))
      }

      cardsState.update { cardRecords }
    }
  }

  suspend fun encryptAndSaveCards() {
    val cardRecords = cardsState.value.mapValues {
      CardDataWithId(it.key, encryptCard(it.value.plainData))
    }

    repo.setCards(CardRecords(cardRecords.toPersistentMap()))
  }

  suspend fun decryptAndSaveCards() {
    val cardRecords = cardsState.value.mapValues {
      CardDataWithId(it.key, CardData.Unencrypted(it.value.plainData))
    }

    repo.setCards(CardRecords(cardRecords.toPersistentMap()))
  }

  suspend fun encryptAndAddOrUpdateCard(cardRecord: CardRecord) {
    val cardData = encryptCard(cardRecord.plainData)
    repo.addCard(CardDataWithId(cardRecord.id, cardData))

    if (userState.value != null && cardData is CardData.Encrypted) {
      val encryptedCardForDb = CardEncryptedEncodedWithIdForDb(
        id = cardRecord.id,
        cipherText = cardData.card.cipherText.toEncoded(),
        iv = cardData.card.iv.toEncoded()
      )

      realtimeDb.addOrUpdateCard(encryptedCardForDb)
    }
  }

  suspend fun deleteCard(cardId: String) {
    repo.deleteCard(cardId)

    if (userState.value != null) {
      realtimeDb.deleteCard(cardId)
    }
  }

  suspend fun deleteAllCards() {
    repo.setCards(CardRecords(persistentMapOf()))
  }

  private fun encryptCard(card: Card): CardData {
    return when (authState.value.hasCreatedPin) {
      false -> CardData.Unencrypted(card)
      true -> {
        val dek = authState.value.dek

        if (dek == null) {
          throw IllegalStateException("PIN or DEK is required to encrypt card data")
        }

        val serialized = Json.encodeToString(Card.serializer(), card)
        val encrypted = crypto.encryptData(serialized, dek)

        CardData.Encrypted(CardEncrypted(encrypted.ciphertext, encrypted.iv))
      }
    }
  }

  private fun decryptCardData(cardData: CardData): Card {
    return when (cardData) {
      is CardData.Unencrypted -> cardData.card
      is CardData.Encrypted -> {
        val dek = authState.value.dek

        if (dek == null) {
          throw IllegalStateException("PIN or DEK is required to encrypt card data")
        }

        val encryptedData = EncryptedData(cardData.card.cipherText, cardData.card.iv)
        val decrypted = crypto.decryptData(encryptedData, dek)

        if (decrypted == null || decrypted.isEmpty()) {
          throw IllegalStateException("Failed to decrypt card data")
        }

        Json.decodeFromString(Card.serializer(), decrypted)
      }
    }
  }
}
