package app.cardnest.screens.user.account

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.cardnest.components.toast.AppToast
import app.cardnest.data.user.SignInResult
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import app.cardnest.screens.password.create.CreatePasswordScreen
import app.cardnest.screens.password.sign_in.SignInWithPasswordScreen
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.stateInViewModel
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.delay

class AccountViewModel(private val userManager: UserManager, private val navigator: Navigator) : ViewModel() {
  var isSigningIn by mutableStateOf(false)
    private set

  var isDeleting by mutableStateOf(false)
    private set

  val user = userState.stateInViewModel(null)

  fun signInWithGoogle(ctx: Context) {
    isSigningIn = true
    launchDefault {
      try {
        userManager.signInWithGoogle(ctx).also { continueSignInByPassword(it) }
      } catch (e: Exception) {
        e.toastAndLog("AccountViewModel")
        isSigningIn = false
      }
    }
  }

  fun signOut() {
    launchDefault {
      delay(200)
      userManager.signOut()
    }
  }

  fun deleteUser(ctx: Context) {
    isDeleting = true
    launchDefault {
      try {
        userManager.deleteUser(ctx)
        AppToast.success("Account has been deleted")
      } catch (e: Exception) {
        e.toastAndLog("AccountViewModel")
        isDeleting = false
      }
    }
  }

  private fun continueSignInByPassword(result: SignInResult) {
    when (result) {
      SignInResult.CREATE_PASSWORD -> navigator.push(CreatePasswordScreen())
      SignInResult.ENTER_PASSWORD -> navigator.push(SignInWithPasswordScreen())

      SignInResult.ERROR -> throw Exception("Failed to sign-in")
      SignInResult.SUCCESS -> {}
    }
  }
}

