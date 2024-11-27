package app.cardnest.screens.password.unlock.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.pinData
import app.cardnest.data.user.UserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UnlockWithPasswordHelpViewModel(private val userManager: UserManager) : ViewModel() {
  fun hasCreatedPin(): Boolean {
    return pinData.value != null
  }

  fun signOut() {
    viewModelScope.launch(Dispatchers.IO) {
      delay(200)
      userManager.signOut()
    }
  }
}

