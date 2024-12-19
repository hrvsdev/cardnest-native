package app.cardnest.screens.password.sign_in

import app.cardnest.data.user.UserManager
import app.cardnest.screens.password.VerifyPasswordBaseViewModel
import app.cardnest.screens.user.account.AccountScreen
import cafe.adriel.voyager.navigator.Navigator

class SignInWithPasswordViewModel(private val userManager: UserManager, private val navigator: Navigator) : VerifyPasswordBaseViewModel() {
  fun onSubmit() {
    onVerifyPasswordSubmit("SignInWithPasswordViewModel") {
      userManager.continueSignInByEnteringPassword(it)
      navigator.popUntil { it is AccountScreen }
    }
  }
}

