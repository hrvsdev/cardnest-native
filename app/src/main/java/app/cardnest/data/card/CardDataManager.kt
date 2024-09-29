package app.cardnest.data.card

import android.util.Log
import app.cardnest.data.auth.EncryptedData
import app.cardnest.data.authState
import app.cardnest.data.cardsState
import app.cardnest.data.userState
import app.cardnest.db.card.CardRepository
import app.cardnest.utils.crypto.CryptoManager
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.crypto.SecretKey

@OptIn(ExperimentalCoroutinesApi::class)
class CardDataManager(private val repo: CardRepository, private val crypto: CryptoManager) {
  suspend fun decryptAndCollectCards() {
    getDataFlow().collectLatest {
      val updatedCards: MutableMap<String, CardRecord> = mutableMapOf()

      for ((id, card) in it.cards) {
        val stateCard = cardsState.value[card.id]

        if (stateCard == null || stateCard.modifiedAt < card.modifiedAt) {
          updatedCards[id] = CardRecord(id, decryptCardData(card.data), card.modifiedAt)
        } else {
          updatedCards[id] = stateCard
        }
      }

      cardsState.update { updatedCards }
    }
  }

  suspend fun keepCardsSynced() {
    userState.collectLatest {
      Log.d("CardDataManager", "User state: $it")
      if (it != null && it.isSyncing) {
        combine(repo.getLocalCards(), repo.getRemoteCards()) { local, remote ->
          val mergedIds = local.cards.keys + remote.cards.keys
          mergedIds.associateWith {
            val localCard = local.cards[it]
            val remoteCard = remote.cards[it]

            when {
              localCard == null -> checkNotNull(remoteCard)
              remoteCard == null -> localCard
              localCard.modifiedAt < remoteCard.modifiedAt -> remoteCard
              localCard.modifiedAt > remoteCard.modifiedAt -> localCard
              else -> localCard
            }
          }
        }.distinctUntilChanged().collectLatest {
          Log.d("CardDataManager", "Merged cards: $it")
          CardRecords(it.toPersistentMap()).also {
            repo.setCards(it)
            repo.setRemoteCards(it)
          }
        }
      }
    }
  }

  suspend fun encryptAndSaveCards() {
    val cardRecords = cardsState.value.mapValues {
      CardDataWithId(it.key, encryptCard(it.value.plainData), it.value.modifiedAt)
    }

    repo.setCards(CardRecords(cardRecords.toPersistentMap()))
  }

  suspend fun decryptAndSaveCards() {
    val cardRecords = cardsState.value.mapValues {
      CardDataWithId(it.key, CardData.Unencrypted(it.value.plainData), it.value.modifiedAt)
    }

    repo.setCards(CardRecords(cardRecords.toPersistentMap()))
  }

  suspend fun encryptAndAddOrUpdateCard(cardRecord: CardRecord) {
    val cardData = encryptCard(cardRecord.plainData)
    repo.addOrUpdateCard(CardDataWithId(cardRecord.id, cardData, cardRecord.modifiedAt))
  }

  suspend fun deleteCard(cardId: String) {
    repo.deleteCard(cardId)
  }

  suspend fun deleteAllCards() {
    repo.setCards(CardRecords(persistentMapOf()))
  }

  suspend fun syncCards(dek: SecretKey) {
    val localCards = repo.getLocalCards().first().cards
    val remoteCards = repo.getRemoteCards().first().cards

    val mergedCards = localCards.toMutableMap()

    for (it in remoteCards) {
      val decrypted = decryptCardData(it.value.data as CardData.Encrypted, dek)
      val encryptedWithCurrentDek = encryptCard(decrypted)

      mergedCards[it.key] = CardDataWithId(it.key, encryptedWithCurrentDek, it.value.modifiedAt)
    }

    repo.setLocalCards(CardRecords())
    repo.setRemoteCards(CardRecords(mergedCards.toPersistentMap()))
  }

  private fun encryptCard(card: Card): CardData {
    return when (authState.value.hasCreatedPin) {
      false -> CardData.Unencrypted(card)
      true -> {
        val dek = authState.value.dek

        if (dek == null) {
          throw IllegalStateException("PIN or DEK is required to encrypt card data")
        }

        encryptCard(card, dek)
      }
    }
  }

  private fun encryptCard(card: Card, dek: SecretKey): CardData.Encrypted {
    val serialized = Json.encodeToString(Card.serializer(), card)
    val encrypted = crypto.encryptData(serialized, dek)

    return CardData.Encrypted(CardEncrypted(encrypted.ciphertext, encrypted.iv))
  }

  private fun decryptCardData(cardData: CardData): Card {
    return when (cardData) {
      is CardData.Unencrypted -> cardData.card
      is CardData.Encrypted -> {
        val dek = authState.value.dek

        if (dek == null) {
          throw IllegalStateException("PIN or DEK is required to encrypt card data")
        }

        decryptCardData(cardData, dek)
      }
    }
  }

  private fun decryptCardData(cardData: CardData.Encrypted, dek: SecretKey): Card {
    val encryptedData = EncryptedData(cardData.card.cipherText, cardData.card.iv)
    val decrypted = crypto.decryptData(encryptedData, dek)

    if (decrypted == null || decrypted.isEmpty()) {
      throw IllegalStateException("Failed to decrypt card data")
    }

    return Json.decodeFromString(Card.serializer(), decrypted)
  }

  private fun getDataFlow(): Flow<CardRecords> {
    return userState.flatMapLatest {
      if (it != null && it.isSyncing) repo.getRemoteCards() else repo.getLocalCards()
    }
  }
}
