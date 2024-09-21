package app.cardnest.data.preferences

import kotlinx.serialization.Serializable

@Serializable
data class Preferences(
  val userInterface: UserInterface = UserInterface(),
  val interactions: Interactions = Interactions(),
  val sync: Sync = Sync(),
)

@Serializable
data class UserInterface(
  val maskCardNumber: Boolean = false,
)

@Serializable
data class Interactions(
  val hasSkippedPinSetup: Boolean = false,
)

@Serializable
data class Sync(
  val isSyncing: Boolean = false,
)
