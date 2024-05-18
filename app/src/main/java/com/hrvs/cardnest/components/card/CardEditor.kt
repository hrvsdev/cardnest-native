package com.hrvs.cardnest.components.card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.components.core.AppTextField
import com.hrvs.cardnest.data.CardFocusableField
import com.hrvs.cardnest.state.card.CardEditorViewModel
import com.hrvs.cardnest.utils.card.addCardNumberSpaces

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardEditor(viewModel: CardEditorViewModel) {
  CardPreview(viewModel.card, usePlaceholders = true, focused = viewModel.focused.value)

  Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
    AppTextField(
      label = "Card number",
      placeholder = "Enter card number",
      state = viewModel.number,
      inputTransformation = CardNumberTransformation,
      onFocus = { viewModel.onFocusedChange(CardFocusableField.NUMBER) },
      onBlur = { viewModel.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Number,
      ),
    )

    AppTextField(
      label = "Expiry date",
      placeholder = "Enter expiry date",
      state = viewModel.expiry,
      inputTransformation = ExpiryTransformation,
      onFocus = { viewModel.onFocusedChange(CardFocusableField.EXPIRY) },
      onBlur = { viewModel.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(
        imeAction = ImeAction.Next,
        keyboardType = KeyboardType.Number,
      ),
    )

    AppTextField(
      label = "Cardholder name",
      placeholder = "Enter cardholder name",
      state = viewModel.cardholder,
      onFocus = { viewModel.onFocusedChange(CardFocusableField.CARDHOLDER) },
      onBlur = { viewModel.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
    )

    AppTextField(
      label = "Issuer",
      placeholder = "Enter card issuer/bank",
      state = viewModel.issuer,
      onFocus = { viewModel.onFocusedChange(CardFocusableField.ISSUER) },
      onBlur = { viewModel.onFocusedChange(null) },
      keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
    )

    CardThemeSelector(viewModel.theme.value, viewModel::onThemeChange)
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
