package app.cardnest.data.card

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import app.cardnest.utils.card.addCardNumberSpaces
import app.cardnest.utils.card.defaultCard
import app.cardnest.utils.card.removeCardNumberSpaces

class CardEditorViewModel(card: Card = defaultCard()) : ViewModel() {
  val number = TextFieldState(addCardNumberSpaces(card.number))
  val expiry = TextFieldState(card.expiry)
  val cardholder = TextFieldState(card.cardholder)
  val issuer = TextFieldState(card.issuer)

  val network = mutableStateOf(card.network)
  val theme = mutableStateOf(card.theme)

  val focused = mutableStateOf<CardFocusableField?>(null)

  private val hasSubmitted = mutableStateOf(false)

  val errors by derivedStateOf {
    if (!hasSubmitted.value) return@derivedStateOf CardErrorsState()
    CardErrorsState(
      number = CardNumberError(number.text.length != 19),
      expiry = CardExpiryError(expiry.text.length != 5),
      cardholder = CardholderError(cardholder.text.length < 2),
    )
  }

  val card
    get() = Card(
      number = removeCardNumberSpaces(number.text.toString()),
      expiry = expiry.text.toString(),
      cardholder = cardholder.text.toString(),
      issuer = issuer.text.toString(),
      network = network.value,
      theme = theme.value,
    )

  fun onNetworkChange(network: PaymentNetwork) {
    this.network.value = network
  }

  fun onThemeChange(theme: CardTheme) {
    this.theme.value = theme
  }

  fun onFocusedChange(focused: CardFocusableField?) {
    this.focused.value = focused
  }

  fun onSubmit(next: (data: Card) -> Unit) {
    hasSubmitted.value = true
    if (errors.number.hasError || errors.expiry.hasError || errors.cardholder.hasError) return
    next(card)
  }
}
