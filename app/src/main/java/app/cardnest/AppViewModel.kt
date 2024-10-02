package app.cardnest

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.Connection
import app.cardnest.data.authData
import app.cardnest.data.connectionState
import app.cardnest.data.preferencesState
import app.cardnest.data.userState
import app.cardnest.db.auth.AuthRepository
import app.cardnest.db.preferences.PreferencesRepository
import app.cardnest.firebase.auth.FirebaseUserManager
import app.cardnest.firebase.realtime_db.ConnectionManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
  private val userManager: FirebaseUserManager,
  private val authRepo: AuthRepository,
  private val prefsRepo: PreferencesRepository,
  private val connectionManager: ConnectionManager
) : ViewModel() {
  val isLoading = mutableStateOf(true)
  val hasCreatedPin = authData.map { it.hasCreatedPin }.stateIn(
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
      val user = combine(userManager.getUser(), preferencesState) { user, prefs ->
        user?.copy(isSyncing = prefs.sync.isSyncing)
      }

      user.collectLatest { d -> userState.update { d } }
    }
  }

  private fun initAuth() {
    viewModelScope.launch(Dispatchers.IO) {
      authRepo.getAuthData().collectLatest { d ->
        authData.update { d }
        isLoading.value = false
      }
    }
  }

  private fun initPreferences() {
    viewModelScope.launch(Dispatchers.IO) {
      prefsRepo.getPreferences().collectLatest { d ->
        preferencesState.update { d }
      }
    }
  }

  private fun initConnectionState() {
    viewModelScope.launch(Dispatchers.IO) {
      val connection = combine(userState, connectionManager.getConnectionState()) { user, isConnected ->
        Connection(if (user?.isSyncing == true) isConnected else true, isConnected)
      }

      connection.collectLatest { d -> connectionState.update { d } }
    }
  }
}
