package com.hrvs.cardnest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import com.hrvs.cardnest.state.appViewModelFactory
import com.hrvs.cardnest.state.card.CardsDataViewModel

val LocalTabBarVisibility = compositionLocalOf { mutableStateOf(true) }
val LocalCardsDataVM = compositionLocalOf<CardsDataViewModel> {
  error("CardsDataViewModel not provided")
}

@Composable
fun CompositionProvider(content: @Composable () -> Unit) {
  val showTabBar = remember { mutableStateOf(true) }

  val cardsDataViewModel = viewModel<CardsDataViewModel>(
    factory = appViewModelFactory { CardsDataViewModel(CardNestApp.dataStores.cardsDataStore) }
  )

  CompositionLocalProvider(
    LocalTabBarVisibility provides showTabBar,
    LocalCardsDataVM provides cardsDataViewModel,
    content = content,
  )
}
