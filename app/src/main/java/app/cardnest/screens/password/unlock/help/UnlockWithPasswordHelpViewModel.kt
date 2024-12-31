package app.cardnest.screens.password.unlock.help

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.pinData
import app.cardnest.data.user.UserManager
import app.cardnest.screens.home.HomeScreen
import app.cardnest.utils.extensions.existsStateInViewModel
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class UnlockWithPasswordHelpViewModel(private val userManager: UserManager, private val navigator: Navigator) : ViewModel() {
  val hasCreatedPin = pinData.existsStateInViewModel()

  fun signOut() {
    viewModelScope.launch(Dispatchers.IO) {
      delay(200)
      userManager.signOut()
      navigator.replaceAll(HomeScreen)
    }
  }
}

