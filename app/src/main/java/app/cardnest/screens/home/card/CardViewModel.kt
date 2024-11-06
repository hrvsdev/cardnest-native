package app.cardnest.screens.home.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.cardsState
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CardViewModel(private val dataManager: CardDataManager, private val id: String, private val navigator: Navigator) : ViewModel() {
  val cardWithMeta = cardsState.map { it[id] }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
  )

  fun deleteCard() {
    viewModelScope.launch(Dispatchers.IO) {
      delay(200)

      try {
        dataManager.deleteCard(id)
        navigator.pop()
      } catch (e: Exception) {
        e.toastAndLog("CardViewModel")
      }
    }
  }
}
