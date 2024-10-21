package app.cardnest.screens.user.security

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.components.toast.AppToast
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.preferencesState
import app.cardnest.screens.pin.create.create.CreatePinScreen
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
  private val prefsManager: PreferencesManager,
  private val actions: Actions,
  private val navigator: Navigator,
  private val bottomSheetNavigator: BottomSheetNavigator,
) : ViewModel() {
  val hasCreatedPin = authData.map { it.hasCreatedPin }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = false
  )

  val hasBiometricsEnabled = authData.map { it.hasBiometricsEnabled }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(),
    initialValue = false
  )

  fun onCreatePin() {
    navigator.push(CreatePinScreen())
    actions.setAfterPinCreated {
      navigator.popUntil { it is SecurityScreen }
      if (hasCreatedPin.value) {
        AppToast.success("PIN has been updated")
      }
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
      if (preferencesState.value.sync.isSyncing) {
        prefsManager.setSync(false)
      } else {
        cardDataManager.decryptAndSaveCards()
      }
      authManager.removePin()

      navigator.popUntil { it is SecurityScreen }
    }
  }

  fun getShowBiometricsSwitch(ctx: FragmentActivity): Boolean {
    return hasBiometricsEnabled.value || (hasCreatedPin.value && authManager.getAreBiometricsAvailable(ctx))
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
