package com.hrvs.cardnest.utils

import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.DisplayCardDetails

fun formatCardViewDetails(card: CardFullProfile, usePlaceholders: Boolean = false): DisplayCardDetails {
  val cardNumber = card.number.let {
    var number = it

    if (usePlaceholders) number = number.padEnd(16, '•')

    number.chunked(4).joinToString(" ")
  }

  val cardExpiry = card.expiry.let {
    val (month, year) = Pair(it.substringBefore('/'), it.substringAfter('/', ""))

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
