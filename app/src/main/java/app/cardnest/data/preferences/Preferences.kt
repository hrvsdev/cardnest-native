package app.cardnest.data.preferences

import androidx.annotation.Keep
import kotlinx.serialization.Serializable

@Keep
@Serializable
data class Preferences(
  val userInterface: UserInterface = UserInterface(),
  val interactions: Interactions = Interactions(),
  val updates: Updates = Updates(),
)

@Keep
@Serializable
data class UserInterface(
  val maskCardNumber: Boolean = false,
)

@Keep
@Serializable
data class Interactions(
  val hasSkippedPinSetup: Boolean = false,
)

@Keep
@Serializable
data class Updates(
  val checkAtLaunch: Boolean = true,
)
