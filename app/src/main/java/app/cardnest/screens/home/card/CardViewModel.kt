package app.cardnest.screens.home.card

import androidx.lifecycle.ViewModel
import app.cardnest.data.card.CardDataManager
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay

class CardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  fun deleteCard(id: String) {
    launchDefault {
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
