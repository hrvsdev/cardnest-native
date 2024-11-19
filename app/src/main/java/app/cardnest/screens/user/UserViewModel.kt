package app.cardnest.screens.user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.components.toast.AppToast
import app.cardnest.data.actions.Actions.afterPinVerified
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.passwordData
import app.cardnest.screens.pin.verify.VerifyPinBeforeActionScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UserViewModel(
  private val cardDataManager: CardDataManager,
  private val navigator: Navigator,
  private val bottomSheetNavigator: BottomSheetNavigator
) : ViewModel() {
  suspend fun deleteAllCards() {
    cardDataManager.deleteAllCards()
    navigator.popUntil { it is UserScreen }
    AppToast.success("Deleted all cards")
  }

  fun onDeleteAllCards() {
    bottomSheetNavigator.hide()
    viewModelScope.launch(Dispatchers.IO) {
      delay(200)

      if (passwordData.value == null) {
        deleteAllCards()
      } else {
        afterPinVerified.set { deleteAllCards() }
        navigator.push(VerifyPinBeforeActionScreen())
      }
    }
  }
}
