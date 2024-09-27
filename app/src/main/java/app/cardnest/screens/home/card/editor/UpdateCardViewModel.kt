package app.cardnest.screens.home.card.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardRecord
import app.cardnest.data.cardsState
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UpdateCardViewModel(
  private val dataManager: CardDataManager,
  private val id: String,
  private val navigator: Navigator
) : ViewModel() {
  fun updateCard(id: String, card: Card) {
    viewModelScope.launch(Dispatchers.IO) {
      dataManager.encryptAndAddOrUpdateCard(CardRecord(id, card, System.currentTimeMillis()))
      navigator.pop()
    }
  }
}
