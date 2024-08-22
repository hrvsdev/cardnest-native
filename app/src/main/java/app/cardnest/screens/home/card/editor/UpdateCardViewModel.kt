package app.cardnest.screens.home.card.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.CardRecord
import app.cardnest.data.card.CardDataManager
import app.cardnest.state.cardsState
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
  val cardRecord = cardsState.map { it[id] }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
  )

  fun updateCard(cardRecord: CardRecord) {
    viewModelScope.launch(Dispatchers.IO) {
      dataManager.encryptAndAddOrUpdateCard(cardRecord)

      navigator.pop()
    }
  }
}
