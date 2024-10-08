package app.cardnest.data.user

import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.data.authState
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.remoteAuthDataState
import kotlinx.coroutines.delay
import javax.crypto.SecretKey

class UserManager(
  private val authManager: AuthManager,
  private val cardDataManager: CardDataManager,
  private val prefsManager: PreferencesManager
) {
  suspend fun setupSync(): SyncResult {
    val isUserNew = waitAndGetRemoteAuthData() == null

    val dek = authState.value.dek
    val pin = authState.value.pin

    if (isUserNew) {
      return if (dek == null) {
        SyncResult.CREATE_PIN
      } else {
        syncDataAndUpdateState(dek)
      }
    }

    if (pin == null) return SyncResult.PREVIOUS_PIN_REQUIRED

    val remoteDek = authManager.getRemoteDek(pin)
    val isLocalPinSameAsRemote = remoteDek != null

    return if (isLocalPinSameAsRemote) {
      syncDataAndUpdateState(remoteDek)
    } else {
      SyncResult.PREVIOUS_PIN_REQUIRED
    }
  }

  suspend fun continueSetupSyncWithDifferentPin(pin: String): SyncResult {
    val remoteDek = authManager.getRemoteDek(pin) ?: return SyncResult.ERROR

    if (!authData.value.hasCreatedPin) {
      authManager.syncStateFromRemoteData(pin, remoteDek)
    }

    return syncDataAndUpdateState(remoteDek)
  }

  private suspend fun syncDataAndUpdateState(dek: SecretKey): SyncResult {
    authManager.syncAuthData()
    cardDataManager.syncCards(dek)

    prefsManager.setSync(true)

    return SyncResult.SUCCESS
  }

  private suspend fun waitAndGetRemoteAuthData(): AuthData? {
    while (remoteAuthDataState.value.hasLoaded == false) {
      delay(200)
    }

    return remoteAuthDataState.value.data
  }
}
