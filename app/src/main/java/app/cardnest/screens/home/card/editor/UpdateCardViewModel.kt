package app.cardnest.screens.home.card.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.utils.extensions.toastAndLog
import app.cardnest.utils.genId
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateCardViewModel(
  private val dataManager: CardDataManager,
  private val id: String,
  private val navigator: Navigator
) : ViewModel() {
  fun updateCard(id: String, card: Card) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        dataManager.encryptAndAddOrUpdateCard(CardUnencrypted(id, card, System.currentTimeMillis()))
        navigator.pop()
      } catch (e: Exception) {
        e.toastAndLog("UpdateCardViewModel")
      }
    }
  }
}
