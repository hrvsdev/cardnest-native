package app.cardnest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.authData
import app.cardnest.data.authDataLoadState
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.remoteAuthData
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import app.cardnest.db.auth.AuthRepository
import app.cardnest.firebase.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
  private val userManager: UserManager,
  private val authRepo: AuthRepository,
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
    initRemoteAuth()
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
      authRepo.getLocalAuthData().collectLatest { d ->
        authData.update { d }
        authDataLoadState.update { it.copy(hasLocalLoaded = true) }
      }
    }
  }

  private fun initRemoteAuth() {
    viewModelScope.launch(Dispatchers.IO) {
      userState.collectLatest {
        if (it != null) {
          authRepo.getRemoteAuthData().collectLatest { d ->
            remoteAuthData.update { d }
            authDataLoadState.update { it.copy(hasRemoteLoaded = true) }
          }
        } else {
          remoteAuthData.update { null }
          authDataLoadState.update { it.copy(hasRemoteLoaded = false) }
        }
      }
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
