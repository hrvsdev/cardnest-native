package com.hrvs.cardnest.screens.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.components.containers.ScreenContainer
import com.hrvs.cardnest.components.containers.TabScreenRoot
import com.hrvs.cardnest.components.header.HeaderTitle


class UserScreen : Screen {
  @Composable
  override fun Content() {
    TabScreenRoot {
      HeaderTitle("You")
      ScreenContainer {

      }
    }
  }
}
