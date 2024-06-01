package com.hrvs.cardnest.screens.add

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.containers.ScreenContainer
import com.hrvs.cardnest.components.containers.TabScreenRoot
import com.hrvs.cardnest.components.header.HeaderTitle

class AddCardScreen : Screen {
  @Composable
  override fun Content() {
    TabScreenRoot {
      HeaderTitle("Add Card")
      ScreenContainer {

      }
    }
  }
}
