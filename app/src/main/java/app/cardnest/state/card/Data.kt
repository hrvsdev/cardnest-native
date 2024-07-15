package app.cardnest.state.card

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.CardRecord
import app.cardnest.db.CardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class CardsDataViewModel(private val repository: CardRepository) : ViewModel() {
  private val _state = repository.getCards()
  val state = _state.mapLatest { it.cards.values.toList() }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList(),
  )

  init {
    Log.i("CardsDataViewModel", "Initializing ...")
  }

  fun addCard(card: CardRecord) {
    viewModelScope.launch(Dispatchers.IO) { repository.addCard(card) }
  }

  fun updateCard(card: CardRecord) {
    viewModelScope.launch(Dispatchers.IO) { repository.updateCard(card) }
  }

  fun deleteCard(id: String) {
    viewModelScope.launch(Dispatchers.IO) { repository.deleteCard(id) }
  }
}
