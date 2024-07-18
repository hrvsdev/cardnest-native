package app.cardnest.state.card

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.CardRecord
import app.cardnest.db.CardRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed interface State<out T> {
  object Loading : State<Nothing>
  data class Success<T>(val data: T) : State<T>
  data class Error(val error: Exception) : State<Nothing>
}

@OptIn(ExperimentalCoroutinesApi::class)
class CardsDataViewModel(private val repository: CardRepository) : ViewModel() {
  private val _state = MutableStateFlow<State<List<CardRecord>>>(State.Loading)
  val state = _state.asStateFlow()

  init {
    Log.i("CardsDataViewModel", "Initializing ...")
    viewModelScope.launch(Dispatchers.IO) {
      try {
        repository.getCards().collectLatest { d ->
          _state.update { State.Success(d.cards.values.toList()) }
        }
      } catch (e: Exception) {
        _state.update { State.Error(e) }
      }
    }
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
