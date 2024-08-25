package app.cardnest.data

import app.cardnest.data.auth.AuthData
import app.cardnest.data.card.CardRecord
import kotlinx.coroutines.flow.MutableStateFlow

data class AuthState(
  val pin: String? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)

val authData = MutableStateFlow(AuthData())
val authState = MutableStateFlow(AuthState())
val cardsState = MutableStateFlow<Map<String, CardRecord>>(emptyMap())
