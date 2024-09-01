package app.cardnest.screens.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.cardsState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: CardDataManager) : ViewModel() {
  val queryState = TextFieldState()

  @OptIn(FlowPreview::class)
  private val queryTextFlow = snapshotFlow { queryState.text }.debounce(150)

  val cardRecordList = combine(cardsState, queryTextFlow) { state, text ->
    state.values.filter { filterCard(it.plainData, text) }
  }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList()
  )

  val totalNoOfCards = cardsState.map { it.size }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = 0
  )

  init {
    viewModelScope.launch(Dispatchers.IO) {
      dataManager.decryptAndCollectCards()
    }
  }

  private fun filterCard(card: Card, query: CharSequence): Boolean {
    if (query.isBlank()) return true
    return searchableFields(card).any { it.contains(query.trim(), ignoreCase = true) }
  }

  private fun searchableFields(card: Card) = listOf(
    card.network.toString(),
    card.issuer,
    card.cardholder,
    card.number,
  )
}
