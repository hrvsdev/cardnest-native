package app.cardnest.screens.user.security

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.serializables.AuthData
import app.cardnest.db.AuthRepository
import app.cardnest.data.card.CardDataManager
import app.cardnest.screens.pin.create.CreatePinScreen
import app.cardnest.screens.pin.verify.VerifyPinBeforeActionScreen
import app.cardnest.state.actions.ActionsViewModel
import app.cardnest.state.auth.BiometricManager
import app.cardnest.state.authState
import app.cardnest.utils.crypto.CryptoManager
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SecurityViewModel(
  private val repository: AuthRepository,
  private val cryptoManager: CryptoManager,
  private val biometricManager: BiometricManager,
  private val cardDataManager: CardDataManager,
  private val actions: ActionsViewModel,
  private val navigator: Navigator,
  private val bottomSheetNavigator: BottomSheetNavigator,
) : ViewModel() {
  val hasCreatedPin = authState.map { it.hasCreatedPin }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = false
  )

  val hasBiometricEnabled = authState.map { it.hasBiometricEnabled }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = false
  )

  fun onCreatePin() {
    navigator.push(CreatePinScreen())
    actions.setAfterPinCreated {
      navigator.popUntilRoot()
    }
  }

  fun onChangePin() {
    navigator.push(VerifyPinBeforeActionScreen())
    actions.setAfterPinVerified {
      onCreatePin()
    }
  }

  fun onRemovePin() {
    viewModelScope.launch(Dispatchers.IO) {
      bottomSheetNavigator.hide()
      delay(200)

      navigator.push(VerifyPinBeforeActionScreen())
    }

    actions.setAfterPinVerified {
      cardDataManager.decryptAndSaveCards()
      removePin()

      navigator.popUntilRoot()
    }
  }

  fun onBiometricSwitchChange(ctx: FragmentActivity) {
    if (hasBiometricEnabled.value) {
      disableBiometric()
    } else {
      enableBiometric(ctx)
    }
  }

  private suspend fun removePin() {
    repository.setAuthData(AuthData())
    authState.update { it.copy(pin = null) }
  }

  private fun enableBiometric(ctx: FragmentActivity) {
    val pin = authState.value.pin ?: return

    viewModelScope.launch(Dispatchers.IO) {
      val androidKey = cryptoManager.getOrCreateAndroidSecretKey()
      val cipher = cryptoManager.getInitializedCipherForEncryption(androidKey)

      biometricManager.authenticate(ctx, cipher, biometricManager.enableBiometricPromptInfo) {
        viewModelScope.launch(Dispatchers.IO) {
          val data = repository.getAuthData().first()
          val encryptedPin = cryptoManager.encryptDataWithCipher(pin, cipher)
          val authData = data.copy(encryptedPin = encryptedPin, hasBiometricsEnabled = true)

          Log.i("AuthDataViewModel", "Enabling biometric ...")

          repository.setAuthData(authData)
        }
      }
    }
  }

  private fun disableBiometric() {
    viewModelScope.launch(Dispatchers.IO) {
      val data = repository.getAuthData().first()
      val authData = data.copy(encryptedPin = null, hasBiometricsEnabled = false)

      repository.setAuthData(authData)
    }
  }
}
