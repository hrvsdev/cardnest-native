package app.cardnest.screens.user.security

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions.afterPinCreated
import app.cardnest.data.actions.Actions.afterPinVerified
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.biometricsData
import app.cardnest.data.passwordData
import app.cardnest.data.pinData
import app.cardnest.screens.password.change.ChangePasswordScreen
import app.cardnest.screens.pin.create.create.CreatePinScreen
import app.cardnest.screens.pin.verify.VerifyPinBeforeActionScreen
import app.cardnest.utils.extensions.stateInViewModel
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class SecurityViewModel(
  private val authManager: AuthManager,
  private val navigator: Navigator,
  private val bottomSheetNavigator: BottomSheetNavigator
) : ViewModel() {
  val hasCreatedPassword = passwordData.map { it != null }.stateInViewModel(false)
  val hasCreatedPin = pinData.map { it != null }.stateInViewModel(false)
  val hasEnabledBiometrics = biometricsData.map { it != null }.stateInViewModel(false)

  fun onChangePassword() {
    navigator.push(ChangePasswordScreen())
  }

  fun onCreatePin() {
    navigator.push(CreatePinScreen())
    afterPinCreated.set {
      navigator.popUntil { it is SecurityScreen }
    }
  }

  fun onChangePin() {
    navigator.push(VerifyPinBeforeActionScreen())
    afterPinVerified.set {
      onCreatePin()
    }
  }

  fun onRemovePin() {
    viewModelScope.launch(Dispatchers.IO) {
      bottomSheetNavigator.hide()
      delay(200)

      navigator.push(VerifyPinBeforeActionScreen())
    }

    afterPinVerified.set {
      authManager.removePin()
      navigator.popUntil { it is SecurityScreen }
    }
  }

  fun getShowBiometricsSwitch(ctx: FragmentActivity): Boolean {
    return hasEnabledBiometrics.value || authManager.getAreBiometricsAvailable(ctx)
  }

  fun onBiometricsSwitchChange(ctx: FragmentActivity) {
    viewModelScope.launch(Dispatchers.IO) {
      if (hasEnabledBiometrics.value) {
        authManager.disableBiometrics()
      } else try {
        authManager.enableBiometrics(ctx, viewModelScope)
      } catch (e: Exception) {
        e.toastAndLog("SecurityViewModel")
      }
    }
  }
}

