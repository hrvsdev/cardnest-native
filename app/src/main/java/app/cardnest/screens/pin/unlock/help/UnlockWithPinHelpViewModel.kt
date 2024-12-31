package app.cardnest.screens.pin.unlock.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.passwordData
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import app.cardnest.screens.home.HomeScreen
import app.cardnest.utils.extensions.existsStateInViewModel
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UnlockWithPinHelpViewModel(private val userManager: UserManager, private val navigator: Navigator) : ViewModel() {
  val hasCreatedPassword = passwordData.existsStateInViewModel()
  val isSignedIn = userState.existsStateInViewModel()

  fun signOut() {
    viewModelScope.launch(Dispatchers.IO) {
      delay(200)
      userManager.signOut()
      navigator.replaceAll(HomeScreen)
    }
  }
}

