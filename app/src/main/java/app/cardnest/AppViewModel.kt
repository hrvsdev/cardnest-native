package app.cardnest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.appDataState
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authState
import app.cardnest.data.passwordData
import app.cardnest.data.pinData
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.password.unlock.UnlockWithNewPasswordScreen
import app.cardnest.screens.password.unlock.UnlockWithPasswordScreen
import app.cardnest.screens.pin.unlock.UnlockWithPinScreen
import app.cardnest.utils.extensions.stateInViewModel
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AppViewModel(
  private val userManager: UserManager,
  private val authManager: AuthManager,
  private val prefsManager: PreferencesManager,
) : ViewModel() {
  var initialScreen by mutableStateOf<Screen?>(null)
    private set

  val isPasswordStale = authState.map { it.isPasswordStale }.stateInViewModel(false)

  init {
    initScreen()
    initUser()
    initAuth()
    initPreferences()
  }

  private fun initScreen() {
    viewModelScope.launch {
      appDataState.first { it.localAuth && it.user && if (userState.value != null) it.remoteAuth else true }
      initialScreen = when {
        isPasswordStale.value -> UnlockWithNewPasswordScreen()
        pinData.value != null -> UnlockWithPinScreen()
        passwordData.value != null -> UnlockWithPasswordScreen()
        else -> HomeScreen
      }
    }
  }

  private fun initUser() {
    viewModelScope.launch(Dispatchers.IO) {
      userManager.collectUser()
    }
  }

  private fun initAuth() {
    viewModelScope.launch(Dispatchers.IO) {
      authManager.collectAuthData()
    }

    viewModelScope.launch(Dispatchers.IO) {
      authManager.collectRemoteAuthData()
    }
  }

  private fun initPreferences() {
    viewModelScope.launch(Dispatchers.IO) {
      prefsManager.collectPreferences()
    }
  }
}
