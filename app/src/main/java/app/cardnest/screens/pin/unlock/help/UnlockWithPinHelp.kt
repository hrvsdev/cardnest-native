package app.cardnest.screens.pin.unlock.help

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.screens.password.unlock.UnlockWithPasswordScreen
import app.cardnest.screens.pin.ForgotPinBottomSheetScreen
import app.cardnest.screens.pin.ForgotPinContext
import app.cardnest.screens.user.account.SignOutBottomSheetScreen
import app.cardnest.utils.extensions.collectValue
import app.cardnest.utils.extensions.open
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class UnlockWithPinHelpScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<UnlockWithPinHelpViewModel> { parametersOf(navigator) }

    val hasCreatedPassword = vm.hasCreatedPassword.collectValue()
    val isSignedIn = vm.isSignedIn.collectValue()

    fun onUnlockWithPassword() {
      navigator.push(UnlockWithPasswordScreen())
    }

    fun onForgotPin() {
      bottomSheetNavigator.open(ForgotPinBottomSheetScreen(ForgotPinContext.UNLOCK, hasCreatedPassword)) {
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
      if (hasCreatedPassword) {
        SettingsGroup("Password") {
          SettingsButton(
            title = "Unlock using your password",
            icon = painterResource(R.drawable.tabler__lock_password),
            isFirst = true,
            isLast = true,
            onClick = ::onUnlockWithPassword
          )
        }
      }

      SettingsGroup("PIN") {
        SettingsButton(
          title = "Forgot PIN?",
          icon = painterResource(R.drawable.tabler__lock_question),
          isFirst = true,
          isLast = true,
          onClick = ::onForgotPin
        )
      }

      if (isSignedIn) {
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
}
