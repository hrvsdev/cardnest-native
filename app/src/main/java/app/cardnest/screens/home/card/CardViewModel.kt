package app.cardnest.screens.home.card

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.CardDataManager
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  fun deleteCard(id: String) {
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
