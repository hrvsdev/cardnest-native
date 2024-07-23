package app.cardnest.state.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.AuthData
import app.cardnest.db.AuthRepository
import app.cardnest.state.card.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val randomString = "SomethingRandom"

data class UiState(val pin: String? = null, val hasCreatedPin: Boolean = false) {
  val isAuthenticated get() = pin != null && pin.isNotEmpty()
}

@OptIn(ExperimentalCoroutinesApi::class)
class AuthDataViewModel(private val repository: AuthRepository) : ViewModel() {
  private val _data = MutableStateFlow<State<AuthData>>(State.Loading)
  private val _uiState = MutableStateFlow(UiState())

  val data = _data.asStateFlow()
  val uiState = combine(_uiState, _data) { ui, data ->
    ui.copy(hasCreatedPin = if (data is State.Success) data.data.hasCreatedPin else false)
  }.stateIn(
    viewModelScope,
    SharingStarted.WhileSubscribed(5000),
    UiState()
  )

  init {
    Log.i("AuthDataViewModel", "Initializing ...")
    viewModelScope.launch(Dispatchers.IO) {
      try {
        repository.getAuthData().collectLatest { d ->
          _data.update { State.Success(d) }
        }
      } catch (e: Exception) {
        _data.update { State.Error(e) }
      }
    }
  }

  fun setPin(pin: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val toCheck = "${randomString}+${pin}"

      repository.setAuthData(AuthData(hasCreatedPin = true, toCheck = toCheck))
      _uiState.update { it.copy(pin = pin) }
    }
  }

  fun verifyPin(pin: String): Boolean {
    return uiState.value.pin == pin
  }

  suspend fun verifyAndSetAppPin(pin: String): Boolean {
    return withContext(Dispatchers.IO) {
      val toCheck = repository.getAuthData().first().toCheck
      val isPinCorrect = "${randomString}+${pin}" == toCheck
      if (isPinCorrect) _uiState.update { it.copy(pin = pin) }
      return@withContext isPinCorrect
    }
  }

  fun removePin() {
    viewModelScope.launch(Dispatchers.IO) {
      repository.setAuthData(AuthData())
      _uiState.update { it.copy(pin = null) }
    }
  }
}
