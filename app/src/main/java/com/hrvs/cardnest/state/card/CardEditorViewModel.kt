package com.hrvs.cardnest.state.card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.hrvs.cardnest.data.CardFocusableField
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.data.PaymentNetwork
import com.hrvs.cardnest.utils.card.addCardNumberSpaces

@OptIn(ExperimentalFoundationApi::class)
class CardEditorViewModel(card: CardFullProfile) : ViewModel() {
  val number = TextFieldState(addCardNumberSpaces(card.number))
  val expiry = TextFieldState(card.expiry)
  val cardholder = TextFieldState(card.cardholder)
  val issuer = TextFieldState(card.issuer)

  val network = mutableStateOf(card.network)
  val theme = mutableStateOf(card.theme)

  val focused = mutableStateOf<CardFocusableField?>(null)

  fun onNetworkChange(network: PaymentNetwork) {
    this.network.value = network
  }

  fun onThemeChange(theme: CardTheme) {
    this.theme.value = theme
  }

  fun onFocusedChange(focused: CardFocusableField?) {
    this.focused.value = focused
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
}
