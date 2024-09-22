package app.cardnest.screens.pin.verify_previous_pin

import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.user.SyncResult
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import app.cardnest.firebase.realtime_db.AuthDbManager
import app.cardnest.screens.pin.PinBaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class VerifyPreviousPinViewModel(
  private val userManager: UserManager,
  private val actions: Actions
) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      val isPinCorrect = userManager.continueSetupSyncWithDifferentPin(pin.value) == SyncResult.SUCCESS

      if (!isPinCorrect) {
        onError()
        return@launch
      }

      actions.afterPinVerified()

      pin.value = ""
    }
  }
}
