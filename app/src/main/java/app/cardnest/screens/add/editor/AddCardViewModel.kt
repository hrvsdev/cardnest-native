package app.cardnest.screens.add.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.CardRecord
import app.cardnest.data.card.CardDataManager
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.home.card.CardViewScreen
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCardViewModel(
  private val dataManager: CardDataManager,
  private val navigator: Navigator
) : ViewModel() {
  fun addCard(cardRecord: CardRecord) {
    viewModelScope.launch(Dispatchers.IO) {
      dataManager.encryptAndAddOrUpdateCard(cardRecord)

      navigator.replaceAll(listOf(HomeScreen, CardViewScreen(cardRecord.id)))
    }
  }
}
