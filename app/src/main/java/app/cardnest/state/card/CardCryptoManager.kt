package app.cardnest.state.card

import app.cardnest.data.CardFullProfile
import app.cardnest.data.serializables.CardData
import app.cardnest.data.serializables.CardDataWithId
import app.cardnest.data.serializables.CardEncrypted
import app.cardnest.data.serializables.CardRecords
import app.cardnest.data.serializables.EncryptedData
import app.cardnest.db.CardRepository
import app.cardnest.state.authState
import app.cardnest.state.cardsState
import app.cardnest.utils.crypto.CryptoManager
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.json.Json
import kotlin.text.toCharArray

class CardCryptoManager(
  private val repository: CardRepository,
  private val cryptoManager: CryptoManager
) {
  suspend fun encryptCards() {
    val cardRecords = cardsState.value.mapValues {
      CardDataWithId(it.key, encryptCardFullProfile(it.value.plainData))
    }

    repository.setCards(CardRecords(cardRecords.toPersistentMap()))
  }

  suspend fun decryptCards() {
    val cardRecords = cardsState.value.mapValues {
      CardDataWithId(it.key, CardData.Unencrypted(it.value.plainData))
    }

    repository.setCards(CardRecords(cardRecords.toPersistentMap()))
  }

  fun encryptCardFullProfile(cardFullProfile: CardFullProfile): CardData {
    return when (authState.value.hasCreatedPin) {
      false -> CardData.Unencrypted(cardFullProfile)
      true -> {
        val latestPin = authState.value.pin

        if (latestPin == null) {
          throw IllegalStateException("Pin is required to encrypt card data")
        }

        val serialized = Json.encodeToString(CardFullProfile.serializer(), cardFullProfile)

        val salt = cryptoManager.generateSalt()
        val key = cryptoManager.deriveKey(latestPin.toCharArray(), salt)
        val encrypted = cryptoManager.encryptData(serialized, key)

        CardData.Encrypted(CardEncrypted(encrypted.ciphertext, encrypted.iv, salt))
      }
    }
  }

  fun decryptCardData(cardData: CardData): CardFullProfile {
    return when (cardData) {
      is CardData.Unencrypted -> cardData.card
      is CardData.Encrypted -> {
        val latestPin = authState.value.pin

        if (latestPin == null) {
          throw IllegalStateException("Pin is required to encrypt card data")
        }

        val encryptedData = EncryptedData(cardData.card.cipherText, cardData.card.iv)

        val key = cryptoManager.deriveKey(latestPin.toCharArray(), cardData.card.salt)
        val decrypted = cryptoManager.decryptData(encryptedData, key)

        Json.decodeFromString(CardFullProfile.serializer(), decrypted)
      }
    }
  }
}

