package app.cardnest.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.CardDataManager
import app.cardnest.state.cardsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: CardDataManager) : ViewModel() {
  val cardRecordList = cardsState.map { it.values.toList() }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  init {
    viewModelScope.launch(Dispatchers.IO) {
      dataManager.decryptAndCollectCards()
    }
  }
}
