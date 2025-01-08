package app.cardnest.screens.password.unlock.help

import androidx.lifecycle.ViewModel
import app.cardnest.data.pinData
import app.cardnest.data.user.UserManager
import app.cardnest.screens.home.HomeScreen
import app.cardnest.utils.extensions.existsStateInViewModel
import app.cardnest.utils.extensions.launchDefault
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay

class UnlockWithPasswordHelpViewModel(private val userManager: UserManager, private val navigator: Navigator) : ViewModel() {
  val hasCreatedPin = pinData.existsStateInViewModel()

  fun signOut() {
    launchDefault {
      delay(200)
      userManager.signOut()
      navigator.replaceAll(HomeScreen)
    }
  }
}

