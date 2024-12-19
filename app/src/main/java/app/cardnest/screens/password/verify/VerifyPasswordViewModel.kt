package app.cardnest.screens.password.verify

import app.cardnest.data.auth.AuthManager
import app.cardnest.screens.password.VerifyPasswordBaseViewModel
import app.cardnest.screens.user.account.AccountScreen
import cafe.adriel.voyager.navigator.Navigator

class VerifyPasswordViewModel(private val authManager: AuthManager, private val navigator: Navigator) : VerifyPasswordBaseViewModel() {
  fun onSubmit() {
    onVerifyPasswordSubmit("VerifyPassword") {
      authManager.verifyPassword(it)
      navigator.popUntil { it is AccountScreen }
    }
  }
}

