package app.cardnest.screens.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.CardsLoadState
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.PaymentNetwork
import app.cardnest.data.cardsLoadState
import app.cardnest.data.cardsState
import app.cardnest.data.preferencesState
import app.cardnest.data.userState
import app.cardnest.utils.extensions.combineStateInViewModel
import app.cardnest.utils.extensions.stateInViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HomeViewModel(private val dataManager: CardDataManager) : ViewModel() {
  val queryState = TextFieldState()

  val userName = userState.map { it?.name }.stateInViewModel(null)
  val cardRecordList = cardsState.map { it.values.toList() }.stateInViewModel(emptyList())
  val maskCardNumber = preferencesState.map { it.userInterface.maskCardNumber }.stateInViewModel(false)

  val loadState = cardsLoadState.stateInViewModel(CardsLoadState())

  val filteredCardIds = combineStateInViewModel(cardRecordList, snapshotFlow { queryState.text }, emptyList()) { state, text ->
    state.mapNotNull { if (filterCard(it.data, text)) it.id else null }
  }

  init {
    initCards()
  }

  private fun initCards() {
    viewModelScope.launch(Dispatchers.IO) {
      dataManager.collectAndDecryptCards()
    }

    viewModelScope.launch(Dispatchers.IO) {
      dataManager.collectRemoteCards()
    }

    viewModelScope.launch(Dispatchers.IO) {
      dataManager.checkAndEncryptOrDecryptCards()
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
