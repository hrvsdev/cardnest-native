package app.cardnest.screens.user.manage_data

import androidx.lifecycle.ViewModel
import app.cardnest.components.toast.AppToast
import app.cardnest.data.actions.Actions.afterPasswordVerified
import app.cardnest.data.actions.Actions.afterPinVerified
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.passwordData
import app.cardnest.data.pinData
import app.cardnest.screens.password.verify.VerifyPasswordScreen
import app.cardnest.screens.pin.verify.VerifyPinScreen
import app.cardnest.utils.extensions.launchDefault
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay

class ManageDataViewModel(private val cardDataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  suspend fun deleteAllCards() {
    cardDataManager.deleteAllCards()
    navigator.popUntil { it is ManageDataScreen }
    AppToast.success("Deleted all cards")
  }

  fun onDeleteAllCards() {
    launchDefault {
      delay(200)

      when {
        passwordData.value != null -> {
          afterPasswordVerified.set { deleteAllCards() }
          navigator.push(VerifyPasswordScreen())
        }

        pinData.value != null -> {
          afterPinVerified.set { deleteAllCards() }
          navigator.push(VerifyPinScreen())
        }

        else -> deleteAllCards()
      }
    }
  }
}

