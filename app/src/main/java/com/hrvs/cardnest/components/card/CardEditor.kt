package com.hrvs.cardnest.components.card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.TextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.components.core.AppTextField
import com.hrvs.cardnest.data.CardFocusableField
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.data.CardTheme
import com.hrvs.cardnest.data.PaymentNetwork
import com.hrvs.cardnest.utils.card.addCardNumberSpaces

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardEditor(
  number: TextFieldState,
  expiry: TextFieldState,
  cardholder: TextFieldState,
  issuer: TextFieldState,

  network: PaymentNetwork,
  onNetworkChange: (PaymentNetwork) -> Unit,

  theme: CardTheme,
  onThemeChange: (CardTheme) -> Unit,

  focused: CardFocusableField?,
  onFocusedChange: (CardFocusableField?) -> Unit,
) {
  CardPreview(
    card = CardFullProfile(
      number = number.text.toString(),
      expiry = expiry.text.toString(),
      cardholder = cardholder.text.toString(),
      issuer = issuer.text.toString(),
      network = network,
      theme = theme,
    ),
    usePlaceholders = true,
    focused = focused,
  )
  Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
    AppTextField(
      label = "Card number",
      placeholder = "Enter card number",
      state = number,
      inputTransformation = CardNumberTransformation,
      onFocus = { onFocusedChange(CardFocusableField.NUMBER) },
      onBlur = { onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Number,
      ),
    )

    AppTextField(
      label = "Expiry date",
      placeholder = "Enter expiry date",
      state = expiry,
      inputTransformation = ExpiryTransformation,
      onFocus = { onFocusedChange(CardFocusableField.EXPIRY) },
      onBlur = { onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Number,
      ),
    )

    AppTextField(
      label = "Cardholder name",
      placeholder = "Enter cardholder name",
      state = cardholder,
      onFocus = { onFocusedChange(CardFocusableField.CARDHOLDER) },
      onBlur = { onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    )

    AppTextField(
      label = "Issuer",
      placeholder = "Enter card issuer/bank",
      state = issuer,
      onFocus = { onFocusedChange(CardFocusableField.ISSUER) },
      onBlur = { onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )

    CardThemeSelector(theme, onThemeChange)
  }
}

@OptIn(ExperimentalFoundationApi::class)
val CardNumberTransformation = InputTransformation.byValue { _, proposed ->
  val filteredValue = proposed.filter { c -> c.isDigit() }.take(16)
  val formattedValue = addCardNumberSpaces(filteredValue.toString())

  formattedValue
}

@OptIn(ExperimentalFoundationApi::class)
val ExpiryTransformation = InputTransformation.byValue { current, proposed ->
  var filteredValue = proposed.filter { c -> c.isDigit() }.take(4)

  if (filteredValue.isNotEmpty() && filteredValue[0].toString().toInt() > 1) {
    filteredValue = "0$filteredValue"
  }

  if (current.endsWith("/") && filteredValue.length == 2) {
    filteredValue = proposed
  } else if (filteredValue.length >= 2) {
    filteredValue = filteredValue.substring(0, 2) + "/" + filteredValue.substring(2)
  }

  filteredValue
}
