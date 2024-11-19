package app.cardnest.data

import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.BiometricsData
import app.cardnest.data.auth.PinData
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.data.preferences.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import javax.crypto.SecretKey

val authData = MutableStateFlow<AuthData?>(null)
val remoteAuthData = MutableStateFlow<AuthData?>(null)

val initialLocalAuthData = MutableStateFlow<AuthData?>(null)
val pinData = MutableStateFlow<PinData?>(null)
val biometricsData = MutableStateFlow<BiometricsData?>(null)

val authDataLoadState = MutableStateFlow(AuthDataLoadState())
val authState = MutableStateFlow(AuthState())

val initialUserState = MutableStateFlow<User?>(null)
val userState = MutableStateFlow<User?>(null)

val cardsState = MutableStateFlow<Map<String, CardUnencrypted>>(emptyMap())

val preferencesState = MutableStateFlow(Preferences())

val connectionState = MutableStateFlow(Connection())

data class AuthDataLoadState(
  val hasLocalLoaded: Boolean = false,
  val hasRemoteLoaded: Boolean = false,
)

data class AuthState(
  val dek: SecretKey? = null,
)

data class User(
  val uid: String,
  val name: String,
  val fullName: String,
)

data class Connection(
  val shouldWrite: Boolean = false,
  val isConnected: Boolean = false
)
