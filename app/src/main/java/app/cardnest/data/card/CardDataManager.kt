package app.cardnest.data.card

import app.cardnest.data.appDataState
import app.cardnest.data.auth.EncryptedData
import app.cardnest.data.authState
import app.cardnest.data.cardsState
import app.cardnest.data.hasEnabledAuth
import app.cardnest.data.userState
import app.cardnest.db.card.CardRepository
import app.cardnest.utils.crypto.CryptoManager
import app.cardnest.utils.extensions.checkNotNull
import app.cardnest.utils.extensions.decoded
import app.cardnest.utils.extensions.encoded
import app.cardnest.utils.extensions.toastAndLog
import app.cardnest.utils.extensions.withDefault
import app.cardnest.utils.extensions.zipWithNext
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json
import javax.crypto.SecretKey

@OptIn(ExperimentalCoroutinesApi::class)
class CardDataManager(private val repo: CardRepository, private val crypto: CryptoManager) {
  suspend fun collectAndDecryptCards() {
    val dataFlow = userState.flatMapLatest {
      appDataState.first { it.areCardsMerging.not() }
      if (it != null) repo.getRemoteCards() else repo.getLocalCards()
    }

    dataFlow.catch { it.toastAndLog("CardDataManager") }.collectLatest {
      val updatedCards: MutableMap<String, CardUnencrypted> = mutableMapOf()

      when (it) {
        is CardRecords.Unencrypted -> it.cards.forEach { updatedCards[it.key] = it.value }
        is CardRecords.Encrypted -> {
          try {
            for ((id, card) in it.cards) {
              val stateCard = cardsState.value[card.id]
              if (stateCard == null || stateCard.modifiedAt < card.modifiedAt) {
                updatedCards[id] = withDefault { decryptToCardUnencrypted(card) }
              } else {
                updatedCards[id] = stateCard
              }
            }
          } catch (e: Exception) {
            e.toastAndLog("CardDataManager")
          }
        }
      }

      cardsState.update { updatedCards }
      appDataState.update { it.copy(cards = true) }
    }
  }

  suspend fun checkAndEncryptOrDecryptCards() {
    hasEnabledAuth.zipWithNext().collectLatest { (previous, current) ->
      if (userState.value == null) {
        val shouldEncrypt = previous == false && current == true
        val shouldDecrypt = previous == true && current == false

        when {
          shouldEncrypt -> encryptCards()
          shouldDecrypt -> decryptCards()
        }
      }
    }
  }

  suspend fun encryptAndAddOrUpdateCard(cardUnencrypted: CardUnencrypted) {
    val cardData = if (hasEnabledAuth.first()) {
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
    val cardRecords = if (hasEnabledAuth.first()) CardRecords.Encrypted() else CardRecords.Unencrypted()
    repo.setCards(cardRecords)
  }

  suspend fun mergeCards() {
    val cards = cardsState.value.mapValues { encryptToCardEncrypted(it.value) } + repo.getRemoteCards().first().cards

    repo.setRemoteCards(CardRecords.Encrypted(cards.toPersistentMap()))
    repo.setLocalCards(CardRecords.Encrypted(cards.toPersistentMap()))

    appDataState.update { it.copy(areCardsMerging = false) }
  }

  suspend fun resetLocalCards() {
    repo.setLocalCards(CardRecords.Unencrypted())
  }

  suspend fun resetRemoteCards() {
    repo.setRemoteCards(CardRecords.Encrypted())
  }

  private suspend fun encryptCards() {
    val cards = cardsState.value.mapValues { encryptToCardEncrypted(it.value) }
    repo.setCards(CardRecords.Encrypted(cards.toPersistentMap()))
  }

  private suspend fun decryptCards() {
    val cards = cardsState.value.mapValues { it.value }
    repo.setCards(CardRecords.Unencrypted(cards.toPersistentMap()))
  }

  private fun encryptToCardEncrypted(card: CardUnencrypted): CardEncrypted {
    val dek = authState.value.dek.checkNotNull { "Encryption Key is not present, restart and unlock app again to encrypt card data" }
    val encryptedData = encryptCard(card.data, dek)

    return CardEncrypted(card.id, encryptedData, card.modifiedAt)
  }

  private fun encryptCard(card: Card, dek: SecretKey): CardEncryptedData {
    val serialized = Json.encodeToString(Card.serializer(), card)
    val encrypted = crypto.encryptData(serialized, dek)

    return CardEncryptedData(encrypted.ciphertext.encoded, encrypted.iv.encoded)
  }

  private fun decryptToCardUnencrypted(card: CardEncrypted): CardUnencrypted {
    val dek = authState.value.dek.checkNotNull { "Encryption Key is not present, restart and unlock app again to decrypt card data" }
    val decrypted = decryptCardData(card.data, dek)

    return CardUnencrypted(card.id, decrypted, card.modifiedAt)
  }

  private fun decryptCardData(cardEncrypted: CardEncryptedData, dek: SecretKey): Card {
    val encryptedData = EncryptedData(cardEncrypted.cipherText.decoded, cardEncrypted.iv.decoded)
    val decryptedString = crypto.decryptData(encryptedData, dek)

    return Json.decodeFromString(Card.serializer(), decryptedString)
  }
}
