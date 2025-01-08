package app.cardnest.screens.user.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.components.settings.SettingsItem
import app.cardnest.utils.extensions.collectValue
import app.cardnest.utils.extensions.open
import cafe.adriel.voyager.core.annotation.InternalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class AccountScreen : Screen {
  @OptIn(InternalVoyagerApi::class)
  @Composable
  override fun Content() {
    val ctx = LocalContext.current
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<AccountViewModel> { parametersOf(navigator) }

    val user = vm.user.collectValue()
    val isSigningIn = vm.isSigningIn
    val isDeleting = vm.isDeleting

    fun onSignInWithGoogle() {
      vm.signInWithGoogle(ctx)
    }

    fun onSignOut() {
      bottomSheetNavigator.open(SignOutBottomSheetScreen()) {
        bottomSheetNavigator.hide()
        vm.signOut()
      }
    }

    fun onDeleteUser() {
      bottomSheetNavigator.open(DeleteAccountBottomSheetScreen()) {
        bottomSheetNavigator.hide()
        vm.deleteUser(ctx)
      }
    }

    SubScreenRoot("Account", backLabel = "Settings", spacedBy = 24.dp) {
      if (user != null) {
        SettingsGroup("User", SIGN_OUT_DESC) {
          SettingsItem(
            title = user.fullName,
            icon = painterResource(R.drawable.tabler__user_circle),
            isFirst = true
          )

          SettingsButton(
            title = "Sign out of your account",
            icon = painterResource(R.drawable.tabler__logout),
            isLast = true,
            onClick = ::onSignOut
          )
        }

        SettingsGroup("Danger Zone", DELETE_ACCOUNT_DESC) {
          SettingsButton(
            title = "Delete account",
            icon = painterResource(R.drawable.tabler__trash),
            isDanger = true,
            isFirst = true,
            isLast = true,
            isLoading = isDeleting,
            onClick = ::onDeleteUser
          )
        }
      } else {
        SettingsGroup("Sign in", SIGN_IN_GOOGLE_DESC) {
          SettingsButton(
            title = "Sign in with Google",
            icon = painterResource(R.drawable.tabler__brand_google_filled),
            isFirst = true,
            isLast = true,
            isLoading = isSigningIn,
            onClick = ::onSignInWithGoogle
          )
        }
      }
    }
  }
}

const val SIGN_IN_GOOGLE_DESC = "Sign in with your account to sync you data across devices."
const val SIGN_OUT_DESC = "Sign out of your account to remove your data from this device."

const val DELETE_ACCOUNT_DESC = "Delete your account to remove your data and account from this device and the server forever."
