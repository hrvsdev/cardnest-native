package app.cardnest.data

import app.cardnest.data.auth.AuthData
import app.cardnest.data.card.CardUnencrypted
import app.cardnest.data.preferences.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import javax.crypto.SecretKey

val authData = MutableStateFlow(AuthData())
val authState = MutableStateFlow(AuthState())

val userState = MutableStateFlow<User?>(null)

val cardsState = MutableStateFlow<Map<String, CardUnencrypted>>(emptyMap())

val preferencesState = MutableStateFlow(Preferences())

val connectionState = MutableStateFlow(Connection())

data class AuthState(
  val pin: String? = null,
  val dek: SecretKey? = null,
)

data class User(
  val uid: String,
  val name: String,
  val fullName: String,
  val isSyncing: Boolean = false
)

data class Connection(
  val shouldWrite: Boolean = false,
  val isConnected: Boolean = false
)

