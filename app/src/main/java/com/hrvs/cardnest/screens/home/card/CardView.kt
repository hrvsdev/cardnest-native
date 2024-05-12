package com.hrvs.cardnest.screens.home.card

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text2.input.InputTransformation
import androidx.compose.foundation.text2.input.rememberTextFieldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.card.CardPreview
import com.hrvs.cardnest.components.card.CardThemeSelector
import com.hrvs.cardnest.components.core.AppTextField
import com.hrvs.cardnest.components.header.SubScreenHeader
import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.ui.theme.ScreenContainer
import com.hrvs.cardnest.ui.theme.TabScreenRoot
import com.hrvs.cardnest.utils.card.addCardNumberSpaces

data class CardViewScreen(val card: CardFullProfile) : Screen {
  @OptIn(ExperimentalFoundationApi::class)
  @Composable
  override fun Content() {
    val number = rememberTextFieldState(addCardNumberSpaces(card.number))
    val expiry = rememberTextFieldState(card.expiry)
    val cardholder = rememberTextFieldState(card.cardholder)
    val issuer = rememberTextFieldState(card.issuer)

    val (theme, onThemeChange) = remember { mutableStateOf(card.theme) }

    TabScreenRoot {
      SubScreenHeader("Card")
      ScreenContainer(32.dp) {
        CardPreview(card.copy(theme = theme))
        Column(verticalArrangement = Arrangement.spacedBy(24.dp)) {
          AppTextField(
            label = "Card number",
            placeholder = "Enter card number",
            state = number,
            inputTransformation = CardNumberTransformation,
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
            keyboardOptions = KeyboardOptions.Default.copy(
              imeAction = ImeAction.Next,
              keyboardType = KeyboardType.Number,
            ),
          )

          AppTextField(
            label = "Cardholder name",
            placeholder = "Enter cardholder name",
            state = cardholder,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
          )

          AppTextField(
            label = "Issuer",
            placeholder = "Enter card issuer/bank",
            state = issuer,
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next)
          )

          CardThemeSelector(theme, onThemeChange)
        }
      }
    }
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
