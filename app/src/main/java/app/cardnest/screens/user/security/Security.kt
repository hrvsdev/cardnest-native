package app.cardnest.screens.user.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.components.settings.SettingsSwitch
import app.cardnest.utils.extensions.collectValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class SecurityScreen : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current as FragmentActivity
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<SecurityViewModel> { parametersOf(navigator, bottomSheetNavigator) }

    val hasCreatedPassword = vm.hasCreatedPassword.collectValue()
    val hasCreatedPin = vm.hasCreatedPin.collectValue()
    val hasEnabledBiometrics = vm.hasEnabledBiometrics.collectValue()

    val CREATE_PIN_DESC = if (hasEnabledBiometrics || hasCreatedPassword) CREATE_PIN_DESC_IF_BIOMETRICS_OR_PASSWORD_EXIST else CREATE_PIN_DESC_IF_NONE_EXIST

    @Suppress("unused")
    fun onBiometricsSwitchChange(checked: Boolean) {
      vm.onBiometricsSwitchChange(ctx)
    }

    SubScreenRoot("Security", backLabel = "Settings", spacedBy = 24.dp) {
      if (hasCreatedPassword) {
        SettingsGroup("Password", PASSWORD_DESC) {
          SettingsButton(
            title = "Change password",
            icon = painterResource(R.drawable.tabler__lock_password),
            isFirst = true,
            isLast = true,
            onClick = vm::onChangePassword,
          )
        }
      }

      SettingsGroup("PIN", if (hasCreatedPin) null else CREATE_PIN_DESC) {
        if (hasCreatedPin) {
          SettingsButton(
            title = "Change PIN",
            icon = painterResource(R.drawable.tabler__password_mobile_phone),
            onClick = vm::onChangePin,
            isFirst = true,
          )

          SettingsButton(
            title = "Remove PIN",
            icon = painterResource(R.drawable.tabler__lock_off),
            onClick = vm::onRemovePin,
            isLast = true,
          )
        } else {
          SettingsButton(
            title = "Create PIN",
            icon = painterResource(R.drawable.tabler__password_mobile_phone),
            onClick = vm::onCreatePin,
            isFirst = true,
            isLast = true,
          )
        }
      }

      if (vm.getShowBiometricsSwitch(ctx)) {
        SettingsGroup("Biometrics", ENABLE_BIOMETRICS_DESC) {
          SettingsSwitch(
            title = "Enable biometrics",
            icon = painterResource(R.drawable.tabler__fingerprint_scan),
            checked = hasEnabledBiometrics,
            onCheckedChange = ::onBiometricsSwitchChange,
            isFirst = true,
            isLast = true,
          )
        }
      }
    }
  }
}

const val PASSWORD_DESC = "Your password encrypts your data to ensure privacy and security."

const val CREATE_PIN_DESC_IF_BIOMETRICS_OR_PASSWORD_EXIST = "Create a PIN to unlock the app along with biometrics or password."
const val CREATE_PIN_DESC_IF_NONE_EXIST = "Create a PIN to encrypt your data to ensure privacy and security."

const val ENABLE_BIOMETRICS_DESC = "Enable biometrics for fast and secure access to the app."
