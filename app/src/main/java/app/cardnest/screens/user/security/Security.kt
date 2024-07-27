package app.cardnest.screens.user.security

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.screens.pin.create.CreatePinScreen
import app.cardnest.screens.pin.verify.VerifyPinBeforeActionScreen
import app.cardnest.state.actions.ActionsViewModel
import app.cardnest.state.auth.AuthDataViewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel

class SecurityScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val authVM = koinViewModel<AuthDataViewModel>()
    val actionsVM = koinViewModel<ActionsViewModel>()

    val scope = rememberCoroutineScope()

    val hasCreatedPin = authVM.uiState.collectAsStateWithLifecycle().value.hasCreatedPin

    fun navigateToCreatePin() {
      navigator.push(CreatePinScreen())
    }

    fun removePin() {
      authVM.removePin()

      navigator.pop()
      navigator.pop()
    }

    fun onCreatePinClick() {
      if (hasCreatedPin) {
        actionsVM.setAfterPinVerified(::navigateToCreatePin)
        navigator.push(VerifyPinBeforeActionScreen(::navigateToCreatePin))
      } else {
        navigateToCreatePin()
      }
    }

    fun onRemovePinConfirmClick() {
      actionsVM.setAfterPinVerified(::removePin)
      scope.launch {
        bottomSheetNavigator.hide()
        delay(200)
        navigator.push(VerifyPinBeforeActionScreen(::removePin))
      }
    }

    fun onRemovePinClick() {
      bottomSheetNavigator.show(
        RemovePinBottomSheetScreen(
          onConfirm = ::onRemovePinConfirmClick,
          onClose = { bottomSheetNavigator.hide() }
        )
      )
    }

    SubScreenRoot("Security", leftIconLabel = "Settings", spacedBy = 24.dp) {
      SettingsGroup("PIN & Biometrics", if (hasCreatedPin) null else CREATE_PASSWORD_DESC) {
        SettingsButton(
          title = if (hasCreatedPin) "Change PIN" else "Create PIN",
          icon = painterResource(R.drawable.tabler__password_mobile_phone),
          onClick = ::onCreatePinClick,
          isFirst = true,
          isLast = true,
        )
      }

      if (hasCreatedPin) {
        SettingsGroup("Danger zone", REMOVE_PASSWORD_DESC) {
          SettingsButton(
            title = "Remove app password",
            icon = painterResource(R.drawable.tabler__lock_open_off),
            onClick = ::onRemovePinClick,
            isDanger = true,
            isFirst = true,
            isLast = true,
          )
        }
      }
    }
  }
}

const val CREATE_PASSWORD_DESC =
  "Creating a PIN will make your data private and secure. You will need to enter the PIN every time you open the app.";

const val REMOVE_PASSWORD_DESC =
  "Removing your app PIN will make all your data accessible to anyone who has access to your device.";
