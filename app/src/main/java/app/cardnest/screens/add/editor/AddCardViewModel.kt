package app.cardnest.screens.add.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.utils.extensions.toastAndLog
import app.cardnest.utils.id.genId
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AddCardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  var isAdding by mutableStateOf(false)
    private set

  fun addCard(card: Card) {
    if (isAdding) return

    viewModelScope.launch(Dispatchers.IO) {
      val isAddingJob = launch {
        delay(200)
        isAdding = true
      }

      val cardWithMeta = CardUnencrypted(genId(), card, System.currentTimeMillis())

      try {
        dataManager.encryptAndAddOrUpdateCard(cardWithMeta)
        navigator.replaceAll(listOf(HomeScreen, CardViewScreen(cardWithMeta)))
      } catch (e: Exception) {
        e.toastAndLog("AddCardViewModel")
      } finally {
        isAddingJob.cancel()
        isAdding = false
      }
    }
  }
}
