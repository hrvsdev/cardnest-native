package app.cardnest

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.db.auth.AuthRepository
import app.cardnest.data.authData
import app.cardnest.data.authState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(private val authRepository: AuthRepository) : ViewModel() {
  val isLoading = mutableStateOf(true)
  val hasCreatedPin = authState.map { it.hasCreatedPin }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  init {
    initAuth()
  }

  private fun initAuth() {
    viewModelScope.launch(Dispatchers.IO) {
      authRepository.getAuthData().collectLatest { d ->
        authData.update { d }
        authState.update {
          it.copy(hasCreatedPin = d.hasCreatedPin, hasBiometricsEnabled = d.hasBiometricsEnabled)
        }

        isLoading.value = false
      }
    }
  }
}
