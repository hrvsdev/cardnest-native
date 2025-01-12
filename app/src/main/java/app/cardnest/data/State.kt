package app.cardnest.data

import app.cardnest.data.auth.BiometricsData
import app.cardnest.data.auth.PasswordData
import app.cardnest.data.auth.PinData
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.data.preferences.Preferences
import app.cardnest.screens.user.app_info.updates.UpdatesState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import javax.crypto.SecretKey

val passwordData = MutableStateFlow<PasswordData?>(null)
val remotePasswordData = MutableStateFlow<PasswordData?>(null)

val pinData = MutableStateFlow<PinData?>(null)
val biometricsData = MutableStateFlow<BiometricsData?>(null)

val authState = MutableStateFlow(AuthState())

val initialUserState = MutableStateFlow<User?>(null)
val userState = MutableStateFlow<User?>(null)

val cardsState = MutableStateFlow<Map<String, CardUnencrypted>>(emptyMap())

val preferencesState = MutableStateFlow(Preferences())

val appDataState = MutableStateFlow(AppDataState())

val updatesState = MutableStateFlow<UpdatesState>(UpdatesState.Idle)

val hasEnabledAuth = combine(passwordData, pinData) { password, pin ->
  password != null || pin != null
}

/**
 * Represents the state of the app data.
 * Each of the fields represents whether the data has been loaded or not unless otherwise specified.
 */
data class AppDataState(
  val user: Boolean = false,
  val localAuth: Boolean = false,
  val remoteAuth: Boolean = false,
  val cards: Boolean = false,
  val areCardsMerging: Boolean = false,
  val prefs : Boolean = false,
)

data class AuthState(
  val dek: SecretKey? = null,
  val isPasswordStale: Boolean = false,
)

data class User(
  val uid: String,
  val name: String,
  val fullName: String,
)

