package app.cardnest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.appDataState
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.biometricsData
import app.cardnest.data.passwordData
import app.cardnest.data.pinData
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.user.UserManager
import app.cardnest.firebase.ConnectionManager
import app.cardnest.screens.biometrics.unlock.UnlockWithBiometricsScreen
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.password.unlock.UnlockWithPasswordScreen
import app.cardnest.screens.pin.unlock.UnlockWithPinScreen
import app.cardnest.utils.extensions.combineStateInViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AppViewModel(
  private val userManager: UserManager,
  private val authManager: AuthManager,
  private val prefsManager: PreferencesManager,
  private val connectionManager: ConnectionManager
) : ViewModel() {
  val initialScreen = combineStateInViewModel(passwordData, pinData, biometricsData, null) { password, pin, biometrics ->
    appDataState.first { it.localAuth }
    when {
      pin != null -> UnlockWithPinScreen()
      password != null -> UnlockWithPasswordScreen()
      biometrics != null -> UnlockWithBiometricsScreen()
      else -> HomeScreen
    }
  }

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
