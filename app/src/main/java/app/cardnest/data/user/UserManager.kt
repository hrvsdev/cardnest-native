package app.cardnest.data.user

import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.data.authDataLoadState
import app.cardnest.data.authState
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.remoteAuthData
import kotlinx.coroutines.flow.first
import javax.crypto.SecretKey

class UserManager(
  private val authManager: AuthManager,
  private val cardDataManager: CardDataManager,
  private val prefsManager: PreferencesManager
) {
  suspend fun setupSync(): SyncResult {
    val remoteAuthData = waitAndGetRemoteAuthData()
    val isUserNew = remoteAuthData == null

    val dek = authState.value.dek
    val pin = authState.value.pin

    if (isUserNew) {
      return if (dek == null) {
        SyncResult.CREATE_PIN
      } else {
        syncDataAndUpdateState(dek)
      }
    }

    if (pin == null) return getPreviousOrNewPinRequired(remoteAuthData)

    val remoteDek = authManager.getRemoteDek(pin)
    val isLocalPinSameAsRemote = remoteDek != null

    return if (isLocalPinSameAsRemote) {
      syncDataAndUpdateState(remoteDek)
    } else {
      getPreviousOrNewPinRequired(remoteAuthData)
    }
  }

  suspend fun continueSetupSyncWithDifferentPin(pin: String): SyncResult {
    val remoteDek = authManager.getRemoteDek(pin) ?: return SyncResult.ERROR

    authManager.syncAuthState(pin, remoteDek)
    return syncDataAndUpdateState(remoteDek)
  }

  private suspend fun syncDataAndUpdateState(dek: SecretKey): SyncResult {
    authManager.syncAuthData()
    cardDataManager.syncCards(dek)

    prefsManager.setSync(true)

    return SyncResult.SUCCESS
  }

  private suspend fun waitAndGetRemoteAuthData(): AuthData? {
    authDataLoadState.first { it.hasRemoteLoaded }
    return remoteAuthData.value
  }

  private fun getPreviousOrNewPinRequired(remoteAuthData: AuthData): SyncResult {
    val authData = authData.value
    return if (authData != null && authData.modifiedAt < remoteAuthData.modifiedAt) {
      SyncResult.NEW_PIN_REQUIRED
    } else {
      SyncResult.PREVIOUS_PIN_REQUIRED
    }
  }
}
