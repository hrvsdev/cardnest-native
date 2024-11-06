package app.cardnest.screens.pin.verify_new_pin

import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.user.SyncResult
import app.cardnest.data.user.UserManager
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.utils.extensions.toastAndLog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class VerifyNewPinViewModel(private val userManager: UserManager, private val actions: Actions) : PinBaseViewModel() {
  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val isPinCorrect = userManager.continueSetupSyncWithDifferentPin(pin.value) == SyncResult.SUCCESS
        if (isPinCorrect.not()) throw Exception()

        actions.afterPinVerified()
      } catch (e: Exception) {
        onError()
        e.toastAndLog("VerifyNewPinViewModel")
      } finally {
        pin.value = ""
      }
    }
  }
}
