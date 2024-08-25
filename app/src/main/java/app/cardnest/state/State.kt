package app.cardnest.state

import app.cardnest.data.serializables.AuthData
import app.cardnest.data.serializables.CardRecord
import kotlinx.coroutines.flow.MutableStateFlow

data class AuthState(
  val pin: String? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricEnabled: Boolean = false
)

val authData = MutableStateFlow(AuthData())
val authState = MutableStateFlow(AuthState())
val cardsState = MutableStateFlow<Map<String, CardRecord>>(emptyMap())
