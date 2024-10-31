package app.cardnest.screens.user.account

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.actions.Actions
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.preferencesState
import app.cardnest.data.user.SyncResult
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import app.cardnest.firebase.auth.FirebaseUserManager
import app.cardnest.screens.pin.create.create.CreatePinBottomSheetScreen
import app.cardnest.screens.pin.create.create.CreatePinScreen
import app.cardnest.screens.pin.verify_new_pin.ProvideNewPinBottomSheetScreen
import app.cardnest.screens.pin.verify_new_pin.VerifyNewPinScreen
import app.cardnest.screens.pin.verify_previous_pin.ProvidePreviousPinBottomSheetScreen
import app.cardnest.screens.pin.verify_previous_pin.VerifyPreviousPinScreen
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
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

  val isSyncing = preferencesState.map { it.sync.isSyncing }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  fun signInWithGoogle(ctx: Context) {
    viewModelScope.launch {
      try {
        isLoading.value = true
        fbUserManager.signInWithGoogle(ctx)
        setupSync()
      } catch (e: Exception) {
        e.toastAndLog("AccountViewModel")
      } finally {
        isLoading.value = false
      }
    }
  }

  fun signOut() {
    viewModelScope.launch(Dispatchers.IO) {
      prefsManager.setSync(false)
      fbUserManager.signOut()
    }
  }

  fun onSyncChange() {
    viewModelScope.launch(Dispatchers.IO) {
      if (preferencesState.value.sync.isSyncing) {
        prefsManager.setSync(false)
      } else {
        setupSync()
      }
    }
  }

  suspend fun setupSync() {
    val result = userManager.setupSync()
    when (result) {
      SyncResult.CREATE_PIN -> bottomSheetNavigator.show(CreatePinBottomSheetScreen(
        onConfirm = { onCreatePin() },
        onCancel = { bottomSheetNavigator.hide() }
      ))

      SyncResult.PREVIOUS_PIN_REQUIRED -> bottomSheetNavigator.show(ProvidePreviousPinBottomSheetScreen(
        onConfirm = { onProvidePin(VerifyPreviousPinScreen()) },
        onCancel = { bottomSheetNavigator.hide() }
      ))

      SyncResult.NEW_PIN_REQUIRED -> bottomSheetNavigator.show(ProvideNewPinBottomSheetScreen(
        onConfirm = { onProvidePin(VerifyNewPinScreen()) },
        onCancel = { bottomSheetNavigator.hide() }
      ))

      SyncResult.ERROR -> Log.e("AccountViewModel", "Error setting up sync")
      SyncResult.SUCCESS -> {}
    }
  }

  private fun onCreatePin() {
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

  private fun onProvidePin(screen: Screen) {
    viewModelScope.launch(Dispatchers.IO) {
      bottomSheetNavigator.hide()
      delay(200)

      navigator.push(screen)
    }

    actions.setAfterPinVerified {
      navigator.popUntil { it is AccountScreen }
    }
  }
}

