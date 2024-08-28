package app.cardnest.data

import app.cardnest.data.auth.AuthData
import app.cardnest.data.card.CardRecord
import kotlinx.coroutines.flow.MutableStateFlow
import javax.crypto.SecretKey

data class AuthState(
  val pin: String? = null,
  val dek: SecretKey? = null,
  val salt: ByteArray? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)

val authData = MutableStateFlow(AuthData())
val authState = MutableStateFlow(AuthState())
val cardsState = MutableStateFlow<Map<String, CardRecord>>(emptyMap())
