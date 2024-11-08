package app.cardnest.screens.user.security

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.components.settings.SettingsSwitch
import app.cardnest.utils.extensions.open
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

    val hasCreatedPin = vm.hasCreatedPin.collectAsStateWithLifecycle().value
    val hasBiometricsEnabled = vm.hasBiometricsEnabled.collectAsStateWithLifecycle().value

    fun onRemovePinClick() {
      bottomSheetNavigator.open(RemovePinBottomSheetScreen(), vm::onRemovePin)
    }

    fun onBiometricsSwitchChange(checked: Boolean) {
      vm.onBiometricsSwitchChange(ctx)
    }

    SubScreenRoot("Security", leftIconLabel = "Settings", spacedBy = 24.dp) {
      SettingsGroup("PIN", if (hasCreatedPin) null else CREATE_PASSWORD_DESC) {
        if (hasCreatedPin) {
          SettingsButton(
            title = "Change PIN",
            icon = painterResource(R.drawable.tabler__password_mobile_phone),
            onClick = vm::onChangePin,
            isFirst = true,
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
        SettingsGroup("Biometrics", BIOMETRICS_DESC) {
          SettingsSwitch(
            title = "Enable biometrics",
            icon = painterResource(R.drawable.tabler__fingerprint_scan),
            checked = hasBiometricsEnabled,
            onCheckedChange = ::onBiometricsSwitchChange,
            isFirst = true,
            isLast = true,
          )
        }
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
  "Creating a PIN will make your data private and secure. You will need to enter the PIN every time you open the app."

const val BIOMETRICS_DESC =
  "Use your fingerprint, face or iris to unlock the app."

const val REMOVE_PASSWORD_DESC =
  "Removing your app PIN will turn sync off and make all your data accessible to anyone who has access to your device."
