package app.cardnest.screens.user.user_interface

import androidx.lifecycle.ViewModel
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.preferencesState
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.stateInViewModel
import kotlinx.coroutines.flow.map

class UserInterfaceViewModel(private val prefsManager: PreferencesManager) : ViewModel() {
  val maskCardNumber = preferencesState.map { it.userInterface.maskCardNumber }.stateInViewModel(false)

  fun onMaskCardNumberChange(maskCardNumber: Boolean) {
    launchDefault {
      prefsManager.setMaskCardNumber(maskCardNumber)
    }
  }
}
