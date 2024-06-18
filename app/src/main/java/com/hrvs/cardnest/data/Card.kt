package com.hrvs.cardnest.data

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
