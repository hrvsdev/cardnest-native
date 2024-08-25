package app.cardnest.screens.user.security

import android.util.Log
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.auth.AuthManager
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
  private val authManager: AuthManager,
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
      authManager.removePin()

      navigator.popUntilRoot()
    }
  }

  fun onBiometricSwitchChange(ctx: FragmentActivity) {
    viewModelScope.launch(Dispatchers.IO) {
      if (hasBiometricEnabled.value) {
        authManager.disableBiometric()
      } else {
        authManager.enableBiometric(ctx)
      }
    }
  }
}
