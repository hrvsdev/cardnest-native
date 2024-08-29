package app.cardnest.screens.user.security

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authState
import app.cardnest.data.card.CardDataManager
import app.cardnest.screens.pin.create.CreatePinScreen
import app.cardnest.screens.pin.verify.VerifyPinBeforeActionScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SecurityViewModel(
  private val authManager: AuthManager,
  private val cardDataManager: CardDataManager,
  private val actions: Actions,
  private val navigator: Navigator,
  private val bottomSheetNavigator: BottomSheetNavigator,
) : ViewModel() {
  val hasCreatedPin = authState.map { it.hasCreatedPin }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = false
  )

  val hasBiometricsEnabled = authState.map { it.hasBiometricsEnabled }.stateIn(
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

  fun onBiometricsSwitchChange(ctx: FragmentActivity) {
    viewModelScope.launch(Dispatchers.IO) {
      if (hasBiometricsEnabled.value) {
        authManager.disableBiometrics()
      } else {
        authManager.enableBiometrics(ctx, viewModelScope)
      }
    }
  }
}