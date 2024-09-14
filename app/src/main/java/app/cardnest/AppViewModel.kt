package app.cardnest

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.authData
import app.cardnest.data.authState
import app.cardnest.data.preferencesState
import app.cardnest.db.auth.AuthRepository
import app.cardnest.db.preferences.PreferencesRepository
import app.cardnest.firebase.auth.FirebaseUserManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(
  private val userManager: FirebaseUserManager,
  private val authRepo: AuthRepository,
  private val prefsRepo: PreferencesRepository
) : ViewModel() {
  val isLoading = mutableStateOf(true)
  val hasCreatedPin = authState.map { it.hasCreatedPin }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  init {
    initUser()
    initAuth()
    initPreferences()
  }

  private fun initUser() {
    userManager.addUserStateListener()
  }

  private fun initAuth() {
    viewModelScope.launch(Dispatchers.IO) {
      authRepo.getAuthData().collectLatest { d ->
        authData.update { d }
        authState.update {
          it.copy(
            salt = d.salt, hasCreatedPin = d.hasCreatedPin, hasBiometricsEnabled = d.hasBiometricsEnabled
          )
        }

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
}
