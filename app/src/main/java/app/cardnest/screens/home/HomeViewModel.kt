package app.cardnest.screens.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.cardsState
import app.cardnest.data.preferencesState
import app.cardnest.data.userState
import app.cardnest.screens.pin.verify_new_pin.ProvideNewPinBottomSheetScreen
import app.cardnest.screens.pin.verify_new_pin.VerifyNewPinScreen
import app.cardnest.utils.extensions.open
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
  private val authManager: AuthManager,
  private val dataManager: CardDataManager,
  private val actions: Actions,
  private val navigator: Navigator,
  private val bottomSheetNavigator: BottomSheetNavigator
) : ViewModel() {
  val queryState = TextFieldState()
  private val queryTextFlow = snapshotFlow { queryState.text }

  val userName = userState.map { it?.name }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
  )

  val cardRecordList = cardsState.map { it.values.toList() }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
  )

  val maskCardNumber = preferencesState.map { it.userInterface.maskCardNumber }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  val filteredCardIds = combine(cardRecordList, queryTextFlow) { state, text ->
    state.filter { filterCard(it.data, text) }.map { it.id }
  }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = emptyList()
  )

  init {
    initCards()
  }

  private fun initCards() {
    viewModelScope.launch(Dispatchers.IO) {
      authManager.hasAuthDataChangedOnAnotherDevice().collectLatest {
        if (it) showProvideNewPinBottomSheet() else dataManager.decryptAndCollectCards()
      }
    }
  }

  private fun showProvideNewPinBottomSheet() {
    bottomSheetNavigator.open(ProvideNewPinBottomSheetScreen(), ::onProvideNewPin)
  }

  private fun onProvideNewPin() {
    viewModelScope.launch(Dispatchers.IO) {
      bottomSheetNavigator.hide()
      delay(200)

      navigator.push(VerifyNewPinScreen())
    }

    actions.setAfterPinVerified {
      navigator.popUntil { it is HomeScreen }
    }
  }

  private fun filterCard(card: Card, query: CharSequence): Boolean {
    if (query.isBlank()) return true
    return searchableFields(card).any { it.contains(query.trim(), ignoreCase = true) }
  }

  private fun searchableFields(card: Card) = listOf(
    card.network.toString(),
    card.issuer,
    card.cardholder,
    card.number,
  )
}
