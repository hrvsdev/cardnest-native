package app.cardnest.data.preferences

import kotlinx.serialization.Serializable

@Serializable
data class Preferences(
  val userInterface: UserInterface = UserInterface(),
  val interactions: Interactions = Interactions()
)

@Serializable
data class UserInterface(
  val maskCardNumber: Boolean = false,
)

@Serializable
data class Interactions(
  val hasSkippedPinSetup: Boolean = false,
)
