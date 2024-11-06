package app.cardnest.screens.add.editor

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

class AddCardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  fun addCard(card: Card) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val id = genId()
        dataManager.encryptAndAddOrUpdateCard(CardUnencrypted(id, card, System.currentTimeMillis()))
        navigator.replaceAll(listOf(HomeScreen, CardViewScreen(id)))
      } catch (e: Exception) {
        e.toastAndLog("AddCardViewModel")
      }
    }
  }
}
