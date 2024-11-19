package app.cardnest.screens.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.PaymentNetwork
import app.cardnest.data.cardsState
import app.cardnest.data.preferencesState
import app.cardnest.data.userState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: CardDataManager) : ViewModel() {
  val queryState = TextFieldState()
  private val queryTextFlow = snapshotFlow { queryState.text }

  val userName = userState.map { it?.name }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
  )

  val cardRecordList = cardsState.map { it.values.toList() }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
  )

  val maskCardNumber = preferencesState.map { it.userInterface.maskCardNumber }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  val filteredCardIds = combine(cardRecordList, queryTextFlow) { state, text ->
    state.filter { filterCard(it.data, text) }.map { it.id }
  }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
  )

  init {
    initCards()
  }

  private fun initCards() {
    viewModelScope.launch(Dispatchers.IO) {
      dataManager.decryptAndCollectCards()
    }

    viewModelScope.launch(Dispatchers.IO) {
      dataManager.mergeAndManageCards()
    }
  }

  private fun filterCard(card: Card, query: CharSequence): Boolean {
    if (query.isBlank()) return true
    return searchableFields(card).any { it.contains(query.trim(), ignoreCase = true) }
  }

  private fun searchableFields(card: Card) = buildList {
    add(card.issuer)
    add(card.cardholder)
    add(card.number)
    if (card.network != PaymentNetwork.OTHER) add(card.network.name)
  }
}
