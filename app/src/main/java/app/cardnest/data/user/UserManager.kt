package app.cardnest.data.user

import app.cardnest.data.User
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
        syncDataAndUpdateState(user, dek)
      } else {
        SyncResult.CREATE_PIN
      }
    }

    if (pin == null) return SyncResult.PREVIOUS_PIN_REQUIRED

    val dbDek = authManager.getDbDek(pin, dbAuthData)
    val isCurrentPinSameAsDb = dbDek != null

    return if (isCurrentPinSameAsDb) {
      syncDataAndUpdateState(user, dbDek)
    } else {
      SyncResult.PREVIOUS_PIN_REQUIRED
    }
  }

  suspend fun continueSetupSyncWithDifferentPin(pin: String): SyncResult {
    val user = userState.value ?: return SyncResult.ERROR
    val dbAuthData = authDb.getAuthData(user.uid) ?: return SyncResult.ERROR
    val dbDek = authManager.getDbDek(pin, dbAuthData) ?: return SyncResult.ERROR

    authManager.setAuthDataAndStateFromDb(dbAuthData, pin, dbDek)

    return syncDataAndUpdateState(user, dbDek)
  }

  private suspend fun syncDataAndUpdateState(user: User, dek: SecretKey): SyncResult {
    authDb.setAuthData(authData.value, user.uid)
    cardDataManager.syncCards(user.uid, dek)

    prefsManager.setSync(true)

    return SyncResult.SUCCESS
  }
}
