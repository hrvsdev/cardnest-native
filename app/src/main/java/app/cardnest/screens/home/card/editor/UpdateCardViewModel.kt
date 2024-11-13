package app.cardnest.screens.home.card.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UpdateCardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  @OptIn(InternalVoyagerApi::class)
  fun updateCard(id: String, card: Card) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val cardWithMeta = CardUnencrypted(id, card, System.currentTimeMillis())
        dataManager.encryptAndAddOrUpdateCard(cardWithMeta)

        navigator.replaceAll(navigator.items.toMutableList().also { it[it.size - 2] = CardViewScreen(cardWithMeta) })
        navigator.pop()

      } catch (e: Exception) {
        e.toastAndLog("UpdateCardViewModel")
      }
    }
  }
}
