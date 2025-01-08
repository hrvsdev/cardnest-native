package app.cardnest.screens.add.editor

import androidx.lifecycle.ViewModel
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.toastAndLog
import app.cardnest.utils.id.genId
import cafe.adriel.voyager.navigator.Navigator

class AddCardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  fun addCard(card: Card) {
    launchDefault {
      val cardWithMeta = CardUnencrypted(genId(), card, System.currentTimeMillis())

      try {
        dataManager.encryptAndAddOrUpdateCard(cardWithMeta)
        navigator.replaceAll(listOf(HomeScreen, CardViewScreen(cardWithMeta)))
      } catch (e: Exception) {
        e.toastAndLog("AddCardViewModel")
      }
    }
  }
}
