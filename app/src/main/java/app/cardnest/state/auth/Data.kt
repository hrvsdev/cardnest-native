package app.cardnest.state.auth

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.AuthData
import app.cardnest.db.AuthRepository
import app.cardnest.state.card.State
import app.cardnest.utils.crypto.CryptoManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class UiState(val pin: String? = null, val hasCreatedPin: Boolean = false)

private val authStateData = MutableStateFlow<State<AuthData>>(State.Loading)
private val uiStateData = MutableStateFlow(UiState())

@OptIn(ExperimentalCoroutinesApi::class)
class AuthDataViewModel(
  val repository: AuthRepository,
  val cryptoManager: CryptoManager
) : ViewModel() {
  val data = authStateData.asStateFlow()
  val uiState = uiStateData.asStateFlow()

  init {
    Log.i("AuthDataViewModel", "Initializing ...")
    viewModelScope.launch(Dispatchers.IO) {
      try {
        repository.getAuthData().collectLatest { d ->
          authStateData.update { State.Success(d) }
          uiStateData.update { it.copy(hasCreatedPin = d.hasCreatedPin) }
        }
      } catch (e: Exception) {
        Log.e("AuthDataViewModel", e.toString())
        authStateData.update { State.Error(e) }
      }
    }
  }

  fun setPin(pin: String): Job {
    return viewModelScope.launch(Dispatchers.IO) {
      val salt = cryptoManager.generateSalt()
      val derivedPinKey = cryptoManager.deriveKey(pin.toCharArray(), salt)

      val randomKeyString = cryptoManager.generateKey().encoded.toString()
      val encryptedRandomKey = cryptoManager.encryptData(randomKeyString, derivedPinKey)

      repository.setAuthData(AuthData(salt, encryptedRandomKey, true, false))
      uiStateData.update { it.copy(pin = pin) }
    }
  }

  suspend fun verifyAndSetAppPin(pin: String): Boolean {
    return withContext(Dispatchers.IO) {
      val data = repository.getAuthData().first()

      val salt = data.salt ?: return@withContext false
      val encryptedRandomKey = data.encryptedRandomKey ?: return@withContext false

      val derivedPinKey = cryptoManager.deriveKey(pin.toCharArray(), salt)
      val randomKeyString = cryptoManager.decryptData(encryptedRandomKey, derivedPinKey)

      val isPinCorrect = randomKeyString.isNotEmpty()

      if (isPinCorrect) {
        uiStateData.update { it.copy(pin = pin) }
      }

      return@withContext isPinCorrect
    }
  }

  fun verifyPin(pin: String): Boolean {
    return uiState.value.pin == pin
  }

  fun removePin() {
    viewModelScope.launch(Dispatchers.IO) {
      repository.setAuthData(AuthData())
      uiStateData.update { it.copy(pin = null) }
    }
  }
}
