package app.cardnest.screens.password.create

import app.cardnest.data.user.UserManager
import app.cardnest.screens.password.NewPasswordBaseViewModel
import app.cardnest.screens.user.account.AccountScreen
import cafe.adriel.voyager.navigator.Navigator

class CreatePasswordViewModel(private val userManager: UserManager, private val navigator: Navigator) : NewPasswordBaseViewModel() {
  fun onSubmit() {
    onNewPasswordSubmit("CreatePasswordViewModel") {
      userManager.continueSignInByCreatingPassword(it)
      navigator.popUntil { it is AccountScreen }
    }
  }
}
