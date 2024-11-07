package app.cardnest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.data.authDataLoadState
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.user.UserManager
import app.cardnest.firebase.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AppViewModel(
  private val userManager: UserManager,
  private val authManager: AuthManager,
  private val prefsManager: PreferencesManager,
  private val connectionManager: ConnectionManager
) : ViewModel() {
  val hasCreatedPin = authData.map { it != null }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  val hasAuthLoaded = authDataLoadState.map { it.hasLocalLoaded }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

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
