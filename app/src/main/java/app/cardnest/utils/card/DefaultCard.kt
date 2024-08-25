package app.cardnest.utils.card

import app.cardnest.data.card.Card
import app.cardnest.data.card.CardTheme
import app.cardnest.data.card.PaymentNetwork

fun defaultCard() = Card(
  number = "",
  expiry = "",
  cardholder = "",
  issuer = "",
  network = PaymentNetwork.VISA,
  theme = CardTheme.entries.random(),
)
