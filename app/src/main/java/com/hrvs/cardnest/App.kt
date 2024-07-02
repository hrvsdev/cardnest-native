package com.hrvs.cardnest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.CurrentScreen
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.hrvs.cardnest.components.containers.appGradient
import com.hrvs.cardnest.components.tabs.TabBar
import com.hrvs.cardnest.screens.add.AddCardScreen
import com.hrvs.cardnest.screens.home.HomeScreen
import com.hrvs.cardnest.screens.user.UserScreen
import com.hrvs.cardnest.state.appViewModelFactory
import com.hrvs.cardnest.state.card.CardsDataViewModel
import com.hrvs.cardnest.ui.theme.TH_BLACK_00
import com.hrvs.cardnest.ui.theme.TH_BLACK_60

val LocalTabBarVisibility = compositionLocalOf { mutableStateOf(true) }
val LocalCardsDataVM = compositionLocalOf<CardsDataViewModel> {
  error("CardsDataViewModel not provided")
}

@Composable
fun App() {
  val showTabBar = remember { mutableStateOf(true) }

  val cardsDataViewModel = viewModel<CardsDataViewModel>(
    factory = appViewModelFactory { CardsDataViewModel(CardNestApp.dataStores.cardsDataStore) }
  )

  CompositionLocalProvider(
    LocalTabBarVisibility provides showTabBar,
    LocalCardsDataVM provides cardsDataViewModel,
  ) {
    TabNavigator(HomeTab) {
      Box(Modifier.background(appGradient)) {
        BottomSheetNavigator(scrimColor = TH_BLACK_60, sheetBackgroundColor = TH_BLACK_00) {
          CurrentTab()
        }
        TabBar()
      }
    }
  }
}

object HomeTab : Tab {
  @Composable
  override fun Content() {
    val showTabBar = LocalTabBarVisibility.current

    Navigator(HomeScreen()) {
      LaunchedEffect(it.lastItem) {
        showTabBar.value = it.lastItem is HomeScreen
      }

      CurrentScreen()
    }
  }

  override val options: TabOptions
    @Composable
    get() = remember {
      TabOptions(
        index = 0u,
        title = "Home",
      )
    }
}

object AddCardTab : Tab {
  @Composable
  override fun Content() {
    val showTabBar = LocalTabBarVisibility.current

    Navigator(AddCardScreen()) {
      LaunchedEffect(it.lastItem) {
        showTabBar.value = it.lastItem is AddCardScreen
      }

      CurrentScreen()
    }
  }

  override val options: TabOptions
    @Composable
    get() = remember {
      TabOptions(
        index = 1u,
        title = "Add Card",
      )
    }
}

object UserTab : Tab {
  @Composable
  override fun Content() {
    val showTabBar = LocalTabBarVisibility.current

    Navigator(UserScreen()) {
      LaunchedEffect(it.lastItem) {
        showTabBar.value = it.lastItem is UserScreen
      }

      CurrentScreen()
    }
  }

  override val options: TabOptions
    @Composable
    get() = remember {
      TabOptions(
        index = 2u,
        title = "User",
      )
    }
}
