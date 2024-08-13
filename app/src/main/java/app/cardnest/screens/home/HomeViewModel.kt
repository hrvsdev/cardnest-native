package app.cardnest.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.CardRecord
import app.cardnest.db.CardRepository
import app.cardnest.state.card.CardCryptoManager
import app.cardnest.state.cardsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
  private val cardRepository: CardRepository,
  private val cardCryptoManager: CardCryptoManager
) : ViewModel() {
  val cardRecordList = cardsState.map { it.values.toList() }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  init {
    initCards()
  }

  private fun initCards() {
    viewModelScope.launch(Dispatchers.IO) {
      cardRepository.getCards().collectLatest {
        val cardRecords = it.cards.mapValues {
          CardRecord(it.key, cardCryptoManager.decryptCardData(it.value.data))
        }

        cardsState.update { cardRecords }
      }
    }
  }
}
