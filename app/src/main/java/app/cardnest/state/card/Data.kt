package app.cardnest.state.card

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.CardFullProfile
import app.cardnest.data.serializables.CardData
import app.cardnest.data.serializables.CardDataWithId
import app.cardnest.data.serializables.CardEncrypted
import app.cardnest.data.serializables.CardRecord
import app.cardnest.data.serializables.CardRecords
import app.cardnest.data.serializables.EncryptedData
import app.cardnest.db.CardRepository
import app.cardnest.state.auth.uiStateData
import app.cardnest.utils.crypto.CryptoManager
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import java.lang.IllegalStateException

sealed interface State<out T> {
  object Loading : State<Nothing>
  data class Success<T>(val data: T) : State<T>
  data class Error(val error: Exception) : State<Nothing>
}

val cardUnencryptedRecordMap = MutableStateFlow<Map<String, CardRecord>>(emptyMap())

var collectCount = 0

class CardsDataViewModel(
  private val repository: CardRepository, private val cryptoManager: CryptoManager
) : ViewModel() {
  val state = cardUnencryptedRecordMap.map { it.values.toList() }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  private val hasCreatedPin get() = uiStateData.value.hasCreatedPin
  private val pin get() = uiStateData.value.pin

  init {
    Log.i("CardsDataViewModel", "Initializing ...")
    viewModelScope.launch(Dispatchers.IO) {
      repository.getCards().collectLatest {
        collectCount++
        Log.i("CardsDataViewModel", "Collecting $collectCount times ...")

        val cardRecords = it.cards.mapValues { CardRecord(it.key, decryptCardData(it.value.data)) }
        cardUnencryptedRecordMap.update { cardRecords }
      }
    }
  }

  fun addCard(card: CardRecord) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        repository.addCard(CardDataWithId(card.id, encryptCardFullProfile(card.plainData)))
      } catch (e: Exception) {
        Log.e("CardsDataViewModel", "Failed to add card", e)
      }
    }
  }

  fun updateCard(card: CardRecord) {
    addCard(card)
  }

  fun deleteCard(id: String) {
    viewModelScope.launch(Dispatchers.IO) {
      repository.deleteCard(id)
    }
  }

  fun encryptCards(): Job {
    return viewModelScope.launch(Dispatchers.IO) {
      val cardRecords = cardUnencryptedRecordMap.value.mapValues {
        CardDataWithId(it.key, encryptCardFullProfile(it.value.plainData))
      }

      repository.setCards(CardRecords(cardRecords.toPersistentMap()))
    }
  }

  fun decryptCards(): Job {
    return viewModelScope.launch(Dispatchers.IO) {
      val cardRecords = cardUnencryptedRecordMap.value.mapValues {
        CardDataWithId(it.key, CardData.Unencrypted(it.value.plainData))
      }

      repository.setCards(CardRecords(cardRecords.toPersistentMap()))
    }
  }

  private fun encryptCardFullProfile(cardFullProfile: CardFullProfile): CardData {
    return when (hasCreatedPin) {
      false -> CardData.Unencrypted(cardFullProfile)
      true -> {
        val latestPin = pin ?: throw IllegalStateException("Pin is required to encrypt card data")

        val serialized = Json.encodeToString(CardFullProfile.serializer(), cardFullProfile)

        val salt = cryptoManager.generateSalt()
        val key = cryptoManager.deriveKey(latestPin.toCharArray(), salt)
        val encrypted = cryptoManager.encryptData(serialized, key)

        CardData.Encrypted(CardEncrypted(encrypted.ciphertext, encrypted.iv, salt))
      }
    }
  }

  private fun decryptCardData(cardData: CardData): CardFullProfile {
    return when (cardData) {
      is CardData.Unencrypted -> cardData.card
      is CardData.Encrypted -> {
        val latestPin = pin ?: throw IllegalStateException("Pin is required to encrypt card data")

        val encryptedData = EncryptedData(cardData.card.cipherText, cardData.card.iv)

        val key = cryptoManager.deriveKey(latestPin.toCharArray(), cardData.card.salt)
        val decrypted = cryptoManager.decryptData(encryptedData, key)

        Json.decodeFromString(CardFullProfile.serializer(), decrypted)
      }
    }
  }
}
