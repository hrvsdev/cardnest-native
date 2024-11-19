package app.cardnest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authDataLoadState
import app.cardnest.data.biometricsData
import app.cardnest.data.passwordData
import app.cardnest.data.pinData
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.user.UserManager
import app.cardnest.firebase.ConnectionManager
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.password.unlock.UnlockWithPasswordScreen
import app.cardnest.screens.pin.unlock.UnlockWithPinScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
  private val userManager: UserManager,
  private val authManager: AuthManager,
  private val prefsManager: PreferencesManager,
  private val connectionManager: ConnectionManager
) : ViewModel() {
  private val initialScreenFlow = combine(passwordData, pinData, biometricsData) { password, pin, biometrics ->
    authDataLoadState.first { it.hasLocalLoaded }
    when {
      pin != null -> UnlockWithPinScreen()
      biometrics != null -> UnlockWithPasswordScreen()
      password != null -> UnlockWithPasswordScreen()
      else -> HomeScreen
    }
  }

  val initialScreen = initialScreenFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  init {
    initUser()
    initAuth()
    initPreferences()
    initConnectionState()
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

  private fun initConnectionState() {
    viewModelScope.launch(Dispatchers.IO) {
      connectionManager.collectConnectionState()
    }
  }
}
