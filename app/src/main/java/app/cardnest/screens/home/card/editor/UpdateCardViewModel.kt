package app.cardnest.screens.home.card.editor

import androidx.lifecycle.ViewModel
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator

class UpdateCardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  fun updateCard(id: String, card: Card) {
    launchDefault {
      val cardWithMeta = CardUnencrypted(id, card, System.currentTimeMillis())

      try {
        dataManager.encryptAndAddOrUpdateCard(cardWithMeta)
        navigator.replaceAll(navigator.items.toMutableList().also { it[it.size - 2] = CardViewScreen(cardWithMeta) })
        navigator.pop()
      } catch (e: Exception) {
        e.toastAndLog("UpdateCardViewModel")
      }
    }
  }
}
