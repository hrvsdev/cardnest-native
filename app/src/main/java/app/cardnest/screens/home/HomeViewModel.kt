package app.cardnest.screens.home

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.snapshotFlow
import androidx.lifecycle.ViewModel
import app.cardnest.data.AppDataState
import app.cardnest.data.appDataState
import app.cardnest.data.card.Card
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.PaymentNetwork
import app.cardnest.data.cardsState
import app.cardnest.data.preferencesState
import app.cardnest.data.userState
import app.cardnest.screens.user.app_info.updates.UpdatesBottomSheetScreen
import app.cardnest.utils.extensions.combineStateInViewModel
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.launchWithIO
import app.cardnest.utils.extensions.stateInViewModel
import app.cardnest.utils.updates.UpdatesManager
import app.cardnest.utils.updates.UpdatesResult
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.flow.map

class HomeViewModel(private val dataManager: CardDataManager, private val updatesManager: UpdatesManager, private val bottomSheetNavigator: BottomSheetNavigator) : ViewModel() {
  val queryState = TextFieldState()

  val userName = userState.map { it?.name }.stateInViewModel(null)
  val cardRecordList = cardsState.map { it.values.toList() }.stateInViewModel(emptyList())
  val maskCardNumber = preferencesState.map { it.userInterface.maskCardNumber }.stateInViewModel(false)

  val loadState = appDataState.stateInViewModel(AppDataState())

  val filteredCardIds = combineStateInViewModel(cardRecordList, snapshotFlow { queryState.text }, emptyList()) { state, text ->
    state.mapNotNull { if (filterCard(it.data, text)) it.id else null }
  }

  init {
    initCards()
    checkForUpdates()
  }

  private fun initCards() {
    launchWithIO {
      dataManager.collectAndDecryptCards()
    }

    launchDefault {
      dataManager.checkAndEncryptOrDecryptCards()
    }
  }

  private fun filterCard(card: Card, query: CharSequence): Boolean {
    if (query.isBlank()) return true
    return searchableFields(card).any { it.contains(query.trim(), ignoreCase = true) }
  }

  private fun searchableFields(card: Card) = buildList {
    add(card.issuer)
    add(card.cardholder)
    add(card.number)
    if (card.network != PaymentNetwork.OTHER) add(card.network.name)
  }

  private fun checkForUpdates() {
    if (preferencesState.value.updates.checkAtLaunch) {
      launchWithIO {
        val result = updatesManager.checkForUpdates()
        if (result is UpdatesResult.UpdateAvailable) {
          bottomSheetNavigator.show(UpdatesBottomSheetScreen(result.version, result.downloadUrl))
        }
      }
    }
  }
}
