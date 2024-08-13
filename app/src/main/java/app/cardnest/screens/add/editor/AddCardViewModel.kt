package app.cardnest.screens.add.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.CardDataWithId
import app.cardnest.data.serializables.CardRecord
import app.cardnest.db.CardRepository
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.state.card.CardCryptoManager
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddCardViewModel(
  private val repository: CardRepository,
  private val cardCryptoManager: CardCryptoManager,
  private val navigator: Navigator
) : ViewModel() {
  fun addCard(cardRecord: CardRecord) {
    viewModelScope.launch(Dispatchers.IO) {
      val encryptedCardData = cardCryptoManager.encryptCardFullProfile(cardRecord.plainData)
      repository.updateCard(CardDataWithId(cardRecord.id, encryptedCardData))

      navigator.replaceAll(listOf(HomeScreen, CardViewScreen(cardRecord.id)))
    }
  }
}
