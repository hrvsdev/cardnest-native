package com.hrvs.cardnest

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.hrvs.cardnest.components.containers.appGradient
import com.hrvs.cardnest.components.tabs.TabBar
import com.hrvs.cardnest.components.tabs.TabButton
import com.hrvs.cardnest.screens.add.AddCardScreen
import com.hrvs.cardnest.screens.home.HomeScreen
import com.hrvs.cardnest.screens.user.UserScreen
import com.hrvs.cardnest.ui.theme.TH_BLACK_00
import com.hrvs.cardnest.ui.theme.TH_BLACK_40
import com.hrvs.cardnest.ui.theme.TH_BLACK_60

@Composable
fun App() {
  var isVisible by remember { mutableStateOf(true) }

  val homeTab = remember { HomeTab(onNavigator = { isVisible = it }) }
  val addCardTab = remember { AddCardTab(onNavigator = { isVisible = it }) }
  val userTab = remember { UserTab(onNavigator = { isVisible = it }) }

  TabNavigator(homeTab) {
    Box(Modifier.background(appGradient)) {
      AppBottomSheetNavigator()

      Box(Modifier.align(Alignment.BottomCenter)) {
        TabBar(isVisible) {
          TabButton(homeTab, painterResource(R.drawable.heroicons__home_solid))
          TabButton(addCardTab, painterResource(R.drawable.heroicons__credit_card_solid))
          TabButton(userTab, painterResource(R.drawable.heroicons__user_circle_solid))
        }
      }
    }
  }
}

@Composable
fun AppBottomSheetNavigator() {
  BottomSheetNavigator(
    scrimColor = TH_BLACK_60,
    sheetBackgroundColor = TH_BLACK_00,
  ) {
    CurrentTab()
  }
}

class HomeTab(
  @Transient
  val onNavigator: (isRoot: Boolean) -> Unit,
) : Tab {
  @Composable
  override fun Content() {
    Navigator(HomeScreen()) {
      LaunchedEffect(it.lastItem) {
        onNavigator(it.lastItem is HomeScreen)
      }
      SlideTransition(it)
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

class AddCardTab(
  @Transient
  val onNavigator: (isRoot: Boolean) -> Unit,
) : Tab {
  @Composable
  override fun Content() {
    Navigator(AddCardScreen()) {
      LaunchedEffect(it.lastItem) {
        onNavigator(it.lastItem is AddCardScreen)
      }
      SlideTransition(it)
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

class UserTab(
  @Transient
  val onNavigator: (isRoot: Boolean) -> Unit,
) : Tab {
  @Composable
  override fun Content() {
    Navigator(UserScreen()) {
      LaunchedEffect(it.lastItem) {
        onNavigator(it.lastItem is UserScreen)
      }
      SlideTransition(it)
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
