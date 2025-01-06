package app.cardnest.utils.card

import app.cardnest.data.card.Card

fun addCardNumberSpaces(number: String) = number.chunked(4).joinToString(" ")
fun removeCardNumberSpaces(number: String) = number.replace(" ", "")

fun formatCardViewDetails(card: Card, usePlaceholders: Boolean = false, maskCardNumber: Boolean = false): DisplayCardDetails {
  val cardNumber = card.number.let {
    var number = removeCardNumberSpaces(it)

    if (usePlaceholders) {
      number = number.padEnd(16, '•')
    }

    if (maskCardNumber) {
      number = number.take(4) + "••••••••" + number.takeLast(4)
    }

    addCardNumberSpaces(number)
  }

  val cardExpiry = card.expiry.let {
    val month = it.substringBefore('/')
    val year = it.substringAfter('/', "")

    if (usePlaceholders) {
      val paddedMonth = month.padEnd(2, '•')
      val paddedYear = year.padEnd(2, '•')
      "$paddedMonth/$paddedYear"
    } else {
      "$month/$year"
    }
  }

  return DisplayCardDetails(
    number = cardNumber,
    expiry = cardExpiry,
    cardholder = card.cardholder.ifBlank { if (usePlaceholders) "Your Name" else "" },
    issuer = card.issuer.ifBlank { if (usePlaceholders) "Issuer/bank" else "" }
  )
}

data class DisplayCardDetails(
  val number: String,
  val expiry: String,
  val cardholder: String,
  val issuer: String
)
