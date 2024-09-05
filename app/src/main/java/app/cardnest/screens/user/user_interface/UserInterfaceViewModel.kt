package app.cardnest.screens.user.user_interface

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.preferencesState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class UserInterfaceViewModel(private val prefsManager: PreferencesManager) : ViewModel() {
  val maskCardNumber = preferencesState.map { it.userInterface.maskCardNumber }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = false
  )

  fun onMaskCardNumberChange(maskCardNumber: Boolean) {
    viewModelScope.launch(Dispatchers.IO) {
      prefsManager.setMaskCardNumber(maskCardNumber)
    }
  }
}
