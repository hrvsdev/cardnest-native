package app.cardnest.screens.pin.enter

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.db.AuthRepository
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.pin.PinBaseViewModel
import app.cardnest.state.auth.BiometricManager
import app.cardnest.state.authState
import app.cardnest.utils.crypto.CryptoManager
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EnterPinViewModel(
  private val authManager: AuthManager,
  private val navigator: Navigator,
) : PinBaseViewModel() {
  val hasBiometricEnabled = authState.map { it.hasBiometricEnabled }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  fun onPinSubmit() {
    viewModelScope.launch(Dispatchers.IO) {
      val isPinCorrect = authManager.verifyAndSetAppPin(pin.value)

      if (!isPinCorrect) {
        onError()
        return@launch
      }

      navigator.replaceAll(HomeScreen)
    }
  }

  fun unlockWithBiometric(ctx: FragmentActivity) {
    viewModelScope.launch(Dispatchers.IO) {
      authManager.unlockWithBiometric(ctx) {
        navigator.replaceAll(HomeScreen)
      }
    }
  }
}
