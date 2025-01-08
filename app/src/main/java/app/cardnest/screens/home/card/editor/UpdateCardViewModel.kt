package app.cardnest.screens.home.card.editor

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UpdateCardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  var isUpdating by mutableStateOf(false)
    private set

  fun updateCard(id: String, card: Card) {
    if (isUpdating) return

    launchDefault {
      val isUpdatingJob = launch {
        delay(200)
        isUpdating = true
      }

      val cardWithMeta = CardUnencrypted(id, card, System.currentTimeMillis())

      try {
        dataManager.encryptAndAddOrUpdateCard(cardWithMeta)
        navigator.replaceAll(navigator.items.toMutableList().also { it[it.size - 2] = CardViewScreen(cardWithMeta) })
        navigator.pop()
      } catch (e: Exception) {
        e.toastAndLog("UpdateCardViewModel")
      } finally {
        isUpdatingJob.cancel()
        isUpdating = false
      }
    }
  }
}
