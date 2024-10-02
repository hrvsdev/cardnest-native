package app.cardnest.data.card

import app.cardnest.data.auth.EncryptedData
import app.cardnest.data.authData
import app.cardnest.data.authState
import app.cardnest.data.cardsState
import app.cardnest.data.userState
import app.cardnest.db.card.CardRepository
import app.cardnest.utils.crypto.CryptoManager
import app.cardnest.utils.extensions.decoded
import app.cardnest.utils.extensions.encoded
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.crypto.SecretKey

@OptIn(ExperimentalCoroutinesApi::class)
class CardDataManager(private val repo: CardRepository, private val crypto: CryptoManager) {
  suspend fun decryptAndCollectCards() {
    getDataFlow().collectLatest {
      val updatedCards: MutableMap<String, CardUnencrypted> = mutableMapOf()

      when (it) {
        is CardRecords.Unencrypted -> it.cards.forEach { updatedCards[it.key] = it.value }
        is CardRecords.Encrypted -> {
          for ((id, card) in it.cards) {
            val stateCard = cardsState.value[card.id]

            if (stateCard == null || stateCard.modifiedAt < card.modifiedAt) {
              updatedCards[id] = decryptToCardUnencrypted(card)
            } else {
              updatedCards[id] = stateCard
            }
          }
        }
      }

      cardsState.update { updatedCards }
    }
  }

  suspend fun encryptAndSaveCards() {
    val cardRecords = cardsState.value.mapValues {
      encryptToCardEncrypted(it.value)
    }

    repo.setCards(CardRecords.Encrypted(cardRecords.toPersistentMap()))
  }

  suspend fun decryptAndSaveCards() {
    val cardRecords = cardsState.value.mapValues {
      CardUnencrypted(it.value.id, it.value.data, it.value.modifiedAt)
    }

    repo.setCards(CardRecords.Unencrypted(cardRecords.toPersistentMap()))
  }

  suspend fun encryptAndAddOrUpdateCard(cardUnencrypted: CardUnencrypted) {
    val cardData = if (authData.value.hasCreatedPin) {
      CardData.Encrypted(encryptToCardEncrypted(cardUnencrypted))
    } else {
      CardData.Unencrypted(cardUnencrypted)
    }

    repo.addOrUpdateCard(cardData)
  }

  suspend fun deleteCard(cardId: String) {
    repo.deleteCard(cardId)
  }

  suspend fun deleteAllCards() {
    val cardRecords = if (authData.value.hasCreatedPin) CardRecords.Encrypted() else CardRecords.Unencrypted()
    repo.setCards(cardRecords)
  }

  suspend fun syncCards(dek: SecretKey) {
    val localCardRecords = repo.getLocalCards().first()
    val remoteCardRecords = repo.getRemoteCards().first()

    val mergedCardRecords = mutableMapOf<String, CardEncrypted>()

    if (localCardRecords is CardRecords.Unencrypted) {
      for (it in localCardRecords.cards) {
        val encrypted = encryptCard(it.value.data, dek)
        mergedCardRecords[it.key] = CardEncrypted(it.value.id, encrypted, it.value.modifiedAt)
      }

      for (it in remoteCardRecords.cards) {
        mergedCardRecords[it.key] = it.value
      }
    }

    if (localCardRecords is CardRecords.Encrypted) {
      for (it in localCardRecords.cards) {
        mergedCardRecords[it.key] = it.value
      }

      for (it in remoteCardRecords.cards) {
        val decrypted = decryptCardData(it.value.data, dek)
        mergedCardRecords[it.key] = encryptToCardEncrypted(CardUnencrypted(it.value.id, decrypted, it.value.modifiedAt))
      }
    }

    repo.setLocalCards(CardRecords.Encrypted())
    repo.setRemoteCards(CardRecords.Encrypted(mergedCardRecords.toPersistentMap()))
  }

  private fun encryptToCardEncrypted(card: CardUnencrypted): CardEncrypted {
    val dek = checkNotNull(authState.value.dek) { "PIN or DEK is required to encrypt card data" }
    val encryptedData = encryptCard(card.data, dek)

    return CardEncrypted(card.id, encryptedData, card.modifiedAt)
  }

  private fun encryptCard(card: Card, dek: SecretKey): CardEncryptedData {
    val serialized = Json.encodeToString(Card.serializer(), card)
    val encrypted = crypto.encryptData(serialized, dek)

    return CardEncryptedData(encrypted.ciphertext.encoded, encrypted.iv.encoded)
  }

  private fun decryptToCardUnencrypted(card: CardEncrypted): CardUnencrypted {
    val dek = checkNotNull(authState.value.dek) { "PIN or DEK is required to decrypt card data" }
    val decrypted = decryptCardData(card.data, dek)

    return CardUnencrypted(card.id, decrypted, card.modifiedAt)
  }

  private fun decryptCardData(cardEncrypted: CardEncryptedData, dek: SecretKey): Card {
    val encryptedData = EncryptedData(cardEncrypted.cipherText.decoded, cardEncrypted.iv.decoded)
    val decryptedString = checkNotNull(crypto.decryptData(encryptedData, dek)) { "Failed to decrypt card data" }

    return Json.decodeFromString(Card.serializer(), decryptedString)
  }

  private fun getDataFlow(): Flow<CardRecords> {
    return userState.flatMapLatest {
      if (it != null && it.isSyncing) repo.getRemoteCards() else repo.getLocalCards()
    }
  }
}
