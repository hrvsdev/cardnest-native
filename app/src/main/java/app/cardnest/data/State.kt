package app.cardnest.data

import app.cardnest.data.auth.AuthData
import app.cardnest.data.card.CardRecord
import app.cardnest.data.preferences.Preferences
import kotlinx.coroutines.flow.MutableStateFlow
import javax.crypto.SecretKey

val authData = MutableStateFlow(AuthData())
val authState = MutableStateFlow(AuthState())

val userState = MutableStateFlow<User?>(null)

val cardsState = MutableStateFlow<Map<String, CardRecord>>(emptyMap())

val preferencesState = MutableStateFlow(Preferences())

data class AuthState(
  val pin: String? = null,
  val dek: SecretKey? = null,
  val salt: ByteArray? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)

data class User(
  val uid: String,
  val name: String,
  val fullName: String,
  val isSyncing: Boolean = false
)
