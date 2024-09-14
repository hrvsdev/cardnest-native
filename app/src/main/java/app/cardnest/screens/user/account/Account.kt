package app.cardnest.screens.user.account

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import cafe.adriel.voyager.core.screen.Screen
import org.koin.androidx.compose.koinViewModel

class AccountScreen : Screen {
  @Composable
  override fun Content() {
    val ctx = LocalContext.current

    val vm = koinViewModel<AccountViewModel>()

    val isLoading = vm.isLoading.value
    val isSignedIn = vm.isSignedIn.collectAsStateWithLifecycle().value

    fun signInWithGoogle() {
      vm.signInWithGoogle(ctx)
    }

    SubScreenRoot("Account", leftIconLabel = "Settings", spacedBy = 24.dp) {
      if (isSignedIn) {
        SettingsGroup("Sign out", SIGN_OUT_DESC) {
          SettingsButton(
            title = "Sign out of your account",
            icon = painterResource(R.drawable.tabler__logout),
            isFirst = true,
            isLast = true,
            onClick = vm::signOut
          )
        }
      } else {
        SettingsGroup("Sign in", SIGN_IN_GOOGLE_DESC) {
          SettingsButton(
            title = "Sign in with Google",
            icon = painterResource(R.drawable.tabler__brand_google),
            isFirst = true,
            isLast = true,
            isLoading = isLoading,
            onClick = ::signInWithGoogle
          )
        }
      }
    }
  }
}

const val SIGN_IN_GOOGLE_DESC = "Sign in with your account to sync you data across devices."
const val SIGN_OUT_DESC = "Sign out of your account to remove your data from this device."
