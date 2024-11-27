package app.cardnest.screens.password.unlock.help

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.screens.pin.unlock.UnlockWithPinScreen
import app.cardnest.screens.user.account.SignOutBottomSheetScreen
import app.cardnest.utils.extensions.open
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UnlockWithPasswordHelpScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<UnlockWithPasswordHelpViewModel> { parametersOf(navigator) }

    val hasCreatedPin = vm.hasCreatedPin()

    fun onUnlockWithPin() {
      navigator.popUntil { it is UnlockWithPinScreen }
    }

    fun onForgotPassword() {
      bottomSheetNavigator.open(ForgotPasswordBottomSheetScreen()) {
        bottomSheetNavigator.hide()
      }
    }

    fun onSignOut() {
      bottomSheetNavigator.open(SignOutBottomSheetScreen()) {
        bottomSheetNavigator.hide()
        vm.signOut()
      }
    }

    SubScreenRoot("Help", spacedBy = 24.dp) {
      if (hasCreatedPin) {
        SettingsGroup("PIN") {
          SettingsButton(
            title = "Unlock using your PIN",
            icon = painterResource(R.drawable.tabler__password),
            isFirst = true,
            isLast = true,
            onClick = ::onUnlockWithPin
          )
        }
      }

      SettingsGroup("Password") {
        SettingsButton(
          title = "Forgot password?",
          icon = painterResource(R.drawable.tabler__lock_password),
          isFirst = true,
          isLast = true,
          onClick = ::onForgotPassword
        )
      }

      SettingsGroup("Sign out") {
        SettingsButton(
          title = "Sign out of your account",
          icon = painterResource(R.drawable.tabler__logout),
          isFirst = true,
          isLast = true,
          onClick = ::onSignOut
        )
      }
    }
  }
}
