package com.hrvs.cardnest.state.card

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hrvs.cardnest.data.CardholderError
import com.hrvs.cardnest.data.CardErrorsState
import com.hrvs.cardnest.data.CardExpiryError
import com.hrvs.cardnest.data.CardFocusableField
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.CardNumberError
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.data.PaymentNetwork
import com.hrvs.cardnest.utils.card.addCardNumberSpaces

class CardEditorViewModel(card: CardFullProfile) : ViewModel() {
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
    get() = CardFullProfile(
      number = number.text.toString(),
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

  fun onSubmit(next: (data: CardFullProfile) -> Unit) {
    hasSubmitted.value = true
    if (errors.number.hasError || errors.expiry.hasError || errors.cardholder.hasError) return
    next(card)
  }
}
