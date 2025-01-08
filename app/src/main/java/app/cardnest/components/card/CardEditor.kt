package app.cardnest.components.card

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.byValue
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.cardnest.components.core.AppTextField
import app.cardnest.data.card.CardEditorViewModel
import app.cardnest.data.card.CardFocusableField
import app.cardnest.utils.card.addCardNumberSpaces

@Composable
fun CardEditor(vm: CardEditorViewModel) {
  CardPreview(vm.card, usePlaceholders = true, focused = vm.focused.value)

  Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
    AppTextField(
      label = "Card number",
      placeholder = "Enter card number",
      state = vm.number,
      error = vm.errors.number,
      inputTransformation = CardNumberTransformation,
      onFocus = { vm.onFocusedChange(CardFocusableField.NUMBER) },
      onBlur = { vm.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
    )

    AppTextField(
      label = "Expiry date",
      placeholder = "Enter expiry date",
      state = vm.expiry,
      inputTransformation = ExpiryTransformation,
      error = vm.errors.expiry,
      onFocus = { vm.onFocusedChange(CardFocusableField.EXPIRY) },
      onBlur = { vm.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
    )

    AppTextField(
      label = "Cardholder name",
      placeholder = "Enter cardholder name",
      state = vm.cardholder,
      error = vm.errors.cardholder,
      onFocus = { vm.onFocusedChange(CardFocusableField.CARDHOLDER) },
      onBlur = { vm.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    )

    AppTextField(
      label = "CVV",
      placeholder = "Enter CVV",
      state = vm.cvv,
      inputTransformation = CvvTransformation,
      error = vm.errors.cvv,
      onFocus = { vm.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next, keyboardType = KeyboardType.Number),
    )

    AppTextField(
      label = "Issuer",
      placeholder = "Enter card issuer/bank",
      state = vm.issuer,
      onFocus = { vm.onFocusedChange(CardFocusableField.ISSUER) },
      onBlur = { vm.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done)
    )

    CardNetworkSelector(vm.network.value, vm::onNetworkChange)
    CardThemeSelector(vm.theme.value, vm::onThemeChange)
  }
}

private val CardNumberTransformation = InputTransformation.byValue { _, proposed ->
  val filteredValue = proposed.filter { c -> c.isDigit() }.take(16)
  val formattedValue = addCardNumberSpaces(filteredValue.toString())

  formattedValue
}

private val ExpiryTransformation = InputTransformation.byValue { current, proposed ->
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

private val CvvTransformation = InputTransformation.byValue { _, proposed ->
  proposed.filter { c -> c.isDigit() }.take(3)
}
