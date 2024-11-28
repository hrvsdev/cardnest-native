package app.cardnest.screens.pin.unlock.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.passwordData
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UnlockWithPinHelpViewModel(private val userManager: UserManager) : ViewModel() {
  fun hasCreatedPassword(): Boolean {
    return passwordData.value != null
  }

  fun isSignedIn(): Boolean {
    return userState.value != null
  }

  fun signOut() {
    viewModelScope.launch(Dispatchers.IO) {
      delay(200)
      userManager.signOut()
    }
  }
}

