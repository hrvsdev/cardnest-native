package app.cardnest.screens.user.account

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.user.SyncResult
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
  private val prefsManager: PreferencesManager,
  private val actions: Actions,
  private val navigator: Navigator,
  private val bottomSheetNavigator: BottomSheetNavigator
) : ViewModel() {
  val isLoading = mutableStateOf(false)

  val user = userState.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = null
  )

  fun signInWithGoogle(ctx: Context) {
    isLoading.value = true
    viewModelScope.launch {
      fbUserManager.signInWithGoogle(ctx, { isLoading.value = false }) {
        viewModelScope.launch(Dispatchers.IO) {
          setupSync(ctx)
          isLoading.value = false
        }
      }
    }
  }

  fun signOut() {
    fbUserManager.signOut()
  }

  fun onSyncChange(ctx: Context, isSyncing: Boolean) {
    viewModelScope.launch(Dispatchers.IO) {
      if (isSyncing) {
        setupSync(ctx)
      } else {
        prefsManager.setSync(false)
      }
    }
  }

  suspend fun setupSync(ctx: Context) {
    val result = userManager.setupSync()

    if (result == SyncResult.ERROR) {
      Log.e("AccountViewModel", "Error setting up sync")
    }

    if (result == SyncResult.CREATE_PIN) {
      bottomSheetNavigator.show(CreatePinBottomSheetScreen(
        onConfirm = { onCreatePin(ctx) },
        onCancel = { bottomSheetNavigator.hide() }
      ))
    }

    if (result == SyncResult.PREVIOUS_PIN_REQUIRED) {
      bottomSheetNavigator.show(ProvidePreviousPinBottomSheetScreen(
        onConfirm = { onProvidePreviousPin() },
        onCancel = { bottomSheetNavigator.hide() }
      ))
    }
  }

  private fun onCreatePin(ctx: Context) {
    viewModelScope.launch(Dispatchers.IO) {
      bottomSheetNavigator.hide()
      delay(200)

      navigator.push(CreatePinScreen())
    }

    actions.setAfterPinCreated {
      navigator.popUntil { it is AccountScreen }
      viewModelScope.launch(Dispatchers.IO) {
        userManager.setupSync()
      }
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

