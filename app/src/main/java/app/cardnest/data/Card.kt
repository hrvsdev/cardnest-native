package app.cardnest.data

import app.cardnest.components.core.AppTextFieldError
import kotlinx.serialization.Serializable

@Serializable
data class CardFullProfile(
  val number: String,
  val expiry: String,
  val cardholder: String,
  val issuer: String,
  val network: PaymentNetwork,
  val theme: CardTheme
)

data class CardErrorsState(
  val number: AppTextFieldError = CardNumberError(false),
  val expiry: AppTextFieldError = CardExpiryError(false),
  val cardholder: AppTextFieldError = CardholderError(false)
)

data class CardNumberError(override val hasError: Boolean) :
  AppTextFieldError("Card number must be exact 16 digits long")

data class CardExpiryError(override val hasError: Boolean) :
  AppTextFieldError("Expiry date must be a valid date in MM/YY format")

data class CardholderError(override val hasError: Boolean) :
  AppTextFieldError("Cardholder name must be at least 2 characters long")

data class DisplayCardDetails(
  val number: String,
  val expiry: String,
  val cardholder: String,
  val issuer: String
)

enum class PaymentNetwork {
  VISA,
  MASTERCARD,
  AMEX,
  DISCOVER,
  DINERS,
  RUPAY,
  OTHER
}

enum class CardFocusableField {
  NUMBER,
  EXPIRY,
  CARDHOLDER,
  ISSUER
}

enum class CardTheme {
  RED,
  ORANGE,
  YELLOW,
  GREEN,
  EMERALD,
  TEAL,
  CYAN,
  SKY,
  BLUE,
  INDIGO,
  VIOLET,
  PURPLE,
  FUCHSIA,
  PINK,
  ROSE,
}
