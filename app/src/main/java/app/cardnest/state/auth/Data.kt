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
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UiState(val pin: String? = null, val hasCreatedPin: Boolean = false)

private val authStateData = MutableStateFlow<State<AuthData>>(State.Loading)
private val uiStateData = MutableStateFlow(UiState())

@OptIn(ExperimentalCoroutinesApi::class)
class AuthDataViewModel(private val repository: AuthRepository) : ViewModel() {
  val data = authStateData.asStateFlow()
  val uiState = uiStateData.asStateFlow()

  init {
    Log.i("AuthDataViewModel", "Initializing ...")
    viewModelScope.launch(Dispatchers.IO) {
      try {
        repository.getAuthData().collectLatest { d ->
          authStateData.value = State.Success(d)
          uiStateData.update { it.copy(hasCreatedPin = d.hasCreatedPin) }
        }
      } catch (e: Exception) {
        Log.e("AuthDataViewModel", e.toString())
        authStateData.value = State.Error(e)
      }
    }
  }

  fun setPin(pin: String) {
    viewModelScope.launch(Dispatchers.IO) {
      val toCheck = hashPin(pin)

      repository.setAuthData(AuthData(hasCreatedPin = true, toCheck = toCheck))
      uiStateData.update { it.copy(pin = pin) }
    }
  }

  fun verifyPin(pin: String): Boolean {
    return uiState.value.pin == pin
  }

  suspend fun verifyAndSetAppPin(pin: String): Boolean {
    return withContext(Dispatchers.IO) {
      val toCheck = repository.getAuthData().first().toCheck
      val isPinCorrect = toCheck == hashPin(pin)

      if (isPinCorrect) {
        uiStateData.update { it.copy(pin = pin) }
      }

      return@withContext isPinCorrect
    }
  }

  fun removePin() {
    viewModelScope.launch(Dispatchers.IO) {
      repository.setAuthData(AuthData())
      uiStateData.update { it.copy(pin = null) }
    }
  }
}

private fun hashPin(pin: String): String {
  return "SomethingRandom+$pin"
}
