package app.cardnest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import app.cardnest.data.appDataState
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authState
import app.cardnest.data.passwordData
import app.cardnest.data.pinData
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.preferencesState
import app.cardnest.data.updatesState
import app.cardnest.data.user.UserManager
import app.cardnest.data.userState
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.password.unlock.UnlockWithNewPasswordScreen
import app.cardnest.screens.password.unlock.UnlockWithPasswordScreen
import app.cardnest.screens.pin.unlock.UnlockWithPinScreen
import app.cardnest.screens.user.app_info.updates.UpdatesState
import app.cardnest.utils.extensions.launchDefault
import app.cardnest.utils.extensions.launchWithIO
import app.cardnest.utils.extensions.log
import app.cardnest.utils.extensions.stateInViewModel
import app.cardnest.utils.updates.UpdatesManager
import app.cardnest.utils.updates.UpdatesResult
import cafe.adriel.voyager.core.screen.Screen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class AppViewModel(private val userManager: UserManager, private val authManager: AuthManager, private val prefsManager: PreferencesManager, private val updatesManager: UpdatesManager) : ViewModel() {
  var initialScreen by mutableStateOf<Screen?>(null)
    private set

  val availableUpdate = updatesState.map { it as? UpdatesState.UpdateAvailable }.stateInViewModel(null)
  val isPasswordStale = authState.map { it.isPasswordStale }.stateInViewModel(false)

  init {
    initScreen()
    initUser()
    initAuth()
    initPreferences()
    checkForUpdates()
  }

  private fun initScreen() {
    launchDefault {
      appDataState.first { it.localAuth && it.user && if (userState.value != null) it.remoteAuth else true }
      initialScreen = when {
        isPasswordStale.value -> UnlockWithNewPasswordScreen()
        pinData.value != null -> UnlockWithPinScreen()
        passwordData.value != null -> UnlockWithPasswordScreen()
        else -> HomeScreen
      }
    }
  }

  private fun initUser() {
    launchDefault {
      userManager.collectUser()
    }
  }

  private fun initAuth() {
    launchWithIO {
      authManager.collectAuthData()
    }

    launchWithIO {
      authManager.collectRemoteAuthData()
    }
  }

  private fun initPreferences() {
    launchWithIO {
      prefsManager.collectPreferences()
    }
  }

  private fun checkForUpdates() {
    launchWithIO {
      appDataState.first { it.prefs }
      if (preferencesState.value.updates.checkAtLaunch) {
        try {
          val result = updatesManager.checkForUpdates()
          val state = when (result) {
            is UpdatesResult.NoUpdate -> UpdatesState.NoUpdate
            is UpdatesResult.UpdateAvailable -> UpdatesState.UpdateAvailable(result.version, result.downloadUrl)
          }

          updatesState.update { state }
        } catch (e: Exception) {
          e.log("AppViewModel")
        }
      }
    }
  }
}
