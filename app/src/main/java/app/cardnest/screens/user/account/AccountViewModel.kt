package app.cardnest.screens.user.account

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.User
import app.cardnest.data.actions.Actions
import app.cardnest.data.authState
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import app.cardnest.firebase.auth.FirebaseUserManager
import app.cardnest.screens.pin.create.CreatePinScreen
import app.cardnest.screens.pin.verify_previous_pin.VerifyPreviousPinScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountViewModel(
  private val userManager: UserManager,
  private val fbUserManager: FirebaseUserManager,
  private val actions: Actions,
  private val navigator: Navigator,
  private val bottomSheetNavigator: BottomSheetNavigator
) : ViewModel() {
  val isLoading = mutableStateOf(false)

  val user = userState.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
  )

  fun signOut() {
    fbUserManager.signOut()
  }

  fun onSignInWithGoogle(ctx: Context) {
    if (authState.value.hasCreatedPin) {
      signInWithGoogle(ctx)
    } else {
      bottomSheetNavigator.show(CreatePinBottomSheetScreen(
        onConfirm = { createPinBeforeSignInWithGoogle(ctx) },
        onCancel = { bottomSheetNavigator.hide() }
      ))
    }
  }

  private fun signInWithGoogle(ctx: Context) {
    isLoading.value = true
    viewModelScope.launch {
      fbUserManager.signInWithGoogle(ctx, { isLoading.value = false }) {
        viewModelScope.launch(Dispatchers.IO) {
          setupSync(it)
          isLoading.value = false
        }
      }
    }
  }

  private fun createPinBeforeSignInWithGoogle(ctx: Context) {
    viewModelScope.launch(Dispatchers.IO) {
      bottomSheetNavigator.hide()
      delay(200)

      navigator.push(CreatePinScreen())
    }

    actions.setAfterPinCreated {
      navigator.popUntil { it is AccountScreen }
      signInWithGoogle(ctx)
    }
  }

  private suspend fun setupSync(user: User) {
    userManager.setupSync {
      bottomSheetNavigator.show(ProvidePreviousPinBottomSheetScreen(
        onConfirm = { onProvidePreviousPin() },
        onCancel = { bottomSheetNavigator.hide() }
      ))
    }
  }

  private fun onProvidePreviousPin() {
    viewModelScope.launch(Dispatchers.IO) {
      bottomSheetNavigator.hide()
      delay(200)

      navigator.push(VerifyPreviousPinScreen())
    }

    actions.setAfterPinVerified {
      navigator.popUntil { it is AccountScreen }
    }
  }
}

