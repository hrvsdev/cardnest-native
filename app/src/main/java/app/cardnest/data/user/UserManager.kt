package app.cardnest.data.user

import app.cardnest.data.User
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.data.authState
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.userState
import app.cardnest.firebase.realtime_db.AuthDbManager
import kotlinx.coroutines.flow.update

class UserManager(
  private val authDb: AuthDbManager,
  private val authManager: AuthManager,
  private val cardDataManager: CardDataManager,
) {
  suspend fun setupSync(askForPin: () -> Unit) {
    val user = userState.value ?: return

    val dbAuthData = authDb.getAuthData(user.uid)
    val isUserNew = dbAuthData == null

    val dek = authState.value.dek ?: return

    if (isUserNew) {
      cardDataManager.syncCards(user.uid, dek)
      authDb.setAuthData(authData.value, user.uid)
      userState.update { user.copy(isSyncing = true) }

      return
    }

    val currentPin = authState.value.pin ?: return
    val dbDek = authManager.getDbDek(currentPin, dbAuthData)

    val isCurrentPinSameAsDb = dbDek != null

    if (isCurrentPinSameAsDb) {
      cardDataManager.syncCards(user.uid, dbDek)
      authDb.setAuthData(authData.value, user.uid)
      userState.update { user.copy(isSyncing = true) }
    } else {
      askForPin()
    }
  }

  suspend fun continueSetupSyncWithDifferentPin(pin: String): Boolean {
    val user = userState.value ?: return false
    val dbAuthData = authDb.getAuthData(user.uid) ?: return false
    val dbDek = authManager.getDbDek(pin, dbAuthData) ?: return false

    cardDataManager.syncCards(user.uid, dbDek)
    userState.update { user.copy(isSyncing = true) }

    return true
  }
}
