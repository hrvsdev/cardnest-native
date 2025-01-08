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
import app.cardnest.screens.pin.verify.VerifyPinScreen
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.open
import app.cardnest.utils.extensions.stateInViewModel
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map

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
    navigator.push(VerifyPinScreen())
    afterPinVerified.set {
      onCreatePin()
    }
  }

  fun onRemovePin() {
    bottomSheetNavigator.open(RemovePinBottomSheetScreen(hasCreatedPassword.value, hasEnabledBiometrics.value)) {
      launchDefault {
        bottomSheetNavigator.hide()
        delay(200)

        navigator.push(VerifyPinScreen())
      }

      afterPinVerified.set {
        authManager.removePin()

        if (hasCreatedPassword.value.not()) {
          authManager.disableBiometrics()
        }

        navigator.popUntil { it is SecurityScreen }
      }
    }
  }

  fun getShowBiometricsSwitch(ctx: FragmentActivity): Boolean {
    return hasEnabledBiometrics.value || authManager.getAreBiometricsAvailable(ctx)
  }

  fun onBiometricsSwitchChange(ctx: FragmentActivity) {
    launchDefault {
      when {
        hasEnabledBiometrics.value -> disableBiometrics()
        checkIfBiometricsBackupIsAvailable() -> enableBiometrics(ctx)
        else -> showEnableBiometricsBottomSheet(ctx)
      }
    }
  }

  private suspend fun enableBiometrics(ctx: FragmentActivity) {
    try {
      authManager.enableBiometrics(ctx, viewModelScope)
    } catch (e: Exception) {
      e.toastAndLog("SecurityViewModel")
    }
  }

  private suspend fun disableBiometrics() {
    authManager.disableBiometrics()
  }

  private fun checkIfBiometricsBackupIsAvailable(): Boolean {
    return hasCreatedPassword.value || hasCreatedPin.value
  }

  private fun showEnableBiometricsBottomSheet(ctx: FragmentActivity) {
    bottomSheetNavigator.open(EnableBiometricsBottomSheetScreen()) {
      launchDefault {
        bottomSheetNavigator.hide()
        delay(200)

        navigator.push(CreatePinScreen())
      }

      afterPinCreated.set {
        enableBiometrics(ctx)
        navigator.popUntil { it is SecurityScreen }
      }
    }
  }
}
