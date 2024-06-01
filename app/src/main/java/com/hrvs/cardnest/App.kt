package com.hrvs.cardnest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.tab.CurrentTab
import cafe.adriel.voyager.navigator.tab.Tab
import cafe.adriel.voyager.navigator.tab.TabNavigator
import cafe.adriel.voyager.navigator.tab.TabOptions
import cafe.adriel.voyager.transitions.SlideTransition
import com.hrvs.cardnest.screens.add.AddCardScreen
import com.hrvs.cardnest.screens.home.HomeScreen
import com.hrvs.cardnest.screens.user.UserScreen

@Composable
fun App() {
  TabNavigator(HomeTab) {
    CurrentTab()
  }
}

object HomeTab : Tab {
  @Composable
  override fun Content() {
    Navigator(HomeScreen()) {
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

object AddCardTab : Tab {
  @Composable
  override fun Content() {
    Navigator(AddCardScreen()) {
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

object UserTab : Tab {
  @Composable
  override fun Content() {
    Navigator(UserScreen()) {
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
