package com.hrvs.cardnest

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.hrvs.cardnest.screens.home.HomeScreen

@Composable
fun App() {
  Navigator(HomeScreen()) {
    SlideTransition(it)
  }
}
