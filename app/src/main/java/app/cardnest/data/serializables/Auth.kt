package app.cardnest.data.serializables

import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
  val toCheck: String = "",
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)
