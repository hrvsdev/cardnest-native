package app.cardnest.screens.user

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.containers.TabScreenRoot
import app.cardnest.components.header.HeaderTitle


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
