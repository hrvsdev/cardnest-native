package app.cardnest.screens.home.card.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.CardDataWithId
import app.cardnest.data.serializables.CardRecord
import app.cardnest.db.CardRepository
import app.cardnest.state.card.CardCryptoManager
import app.cardnest.state.cardsState
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UpdateCardViewModel(
  private val repository: CardRepository,
  private val cardCryptoManager: CardCryptoManager,
  private val id: String,
  private val navigator: Navigator
) : ViewModel() {
  val cardRecord = cardsState.map { it[id] }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
  )

  fun updateCard(cardRecord: CardRecord) {
    viewModelScope.launch(Dispatchers.IO) {
      val encryptedCardData = cardCryptoManager.encryptCardFullProfile(cardRecord.plainData)
      repository.updateCard(CardDataWithId(cardRecord.id, encryptedCardData))

      navigator.pop()
    }
  }
}
