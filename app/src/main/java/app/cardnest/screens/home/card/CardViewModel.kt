package app.cardnest.screens.home.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.db.CardRepository
import app.cardnest.state.cardsState
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardViewModel(
  private val repository: CardRepository,
  private val id: String,
  private val navigator: Navigator
) : ViewModel() {
  val cardRecord = cardsState.map { it[id] }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
  )

  fun deleteCard() {
    viewModelScope.launch(Dispatchers.IO) {
      delay(200)

      repository.deleteCard(id)
      navigator.pop()
    }
  }
}
