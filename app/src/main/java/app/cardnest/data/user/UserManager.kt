package app.cardnest.data.user

import app.cardnest.data.User
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.data.authState
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.userState
import app.cardnest.firebase.realtime_db.AuthDbManager
import javax.crypto.SecretKey

class UserManager(
  private val authDb: AuthDbManager,
  private val authManager: AuthManager,
  private val cardDataManager: CardDataManager,
  private val prefsManager: PreferencesManager
) {
  suspend fun setupSync(): SyncResult {
    val user = userState.value ?: return SyncResult.ERROR

    val dbAuthData = authDb.getAuthData(user.uid)
    val isUserNew = dbAuthData == null

    val dek = authState.value.dek
    val pin = authState.value.pin

    if (isUserNew) {
      return if (dek != null) {
        syncDataAndUpdateState(authData.value, user, dek)
      } else {
        SyncResult.CREATE_PIN
      }
    }

    if (pin == null) return SyncResult.PREVIOUS_PIN_REQUIRED

    val dbDek = authManager.getDbDek(pin, dbAuthData)
    val isCurrentPinSameAsDb = dbDek != null

    return if (isCurrentPinSameAsDb) {
      syncDataAndUpdateState(authData.value, user, dbDek)
    } else {
      SyncResult.PREVIOUS_PIN_REQUIRED
    }
  }

  suspend fun continueSetupSyncWithDifferentPin(pin: String): SyncResult {
    val user = userState.value ?: return SyncResult.ERROR
    val dbAuthData = authDb.getAuthData(user.uid) ?: return SyncResult.ERROR
    val dbDek = authManager.getDbDek(pin, dbAuthData) ?: return SyncResult.ERROR

    val hasCreatedPin = authData.value.hasCreatedPin

    if (!hasCreatedPin) {
      authManager.setAuthDataAndStateFromDb(dbAuthData, pin, dbDek)
    }

    return syncDataAndUpdateState(if (hasCreatedPin) authData.value else dbAuthData, user, dbDek)
  }

  private suspend fun syncDataAndUpdateState(authData: AuthData, user: User, dek: SecretKey): SyncResult {
    authDb.setAuthData(authData, user.uid)
    cardDataManager.syncCards(dek)

    prefsManager.setSync(true)

    return SyncResult.SUCCESS
  }
}
