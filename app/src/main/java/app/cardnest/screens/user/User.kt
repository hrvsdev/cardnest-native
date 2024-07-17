package app.cardnest.screens.user

import androidx.compose.runtime.Composable
import app.cardnest.components.button.AppButton
import cafe.adriel.voyager.core.screen.Screen
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.containers.TabScreenRoot
import app.cardnest.components.header.HeaderTitle
import app.cardnest.screens.NoTransition
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.transitions.ScreenTransition

@Suppress("JavaIoSerializableObjectMustHaveReadResolve")
@OptIn(ExperimentalVoyagerApi::class)
object UserScreen : Screen, ScreenTransition by NoTransition() {
  @Composable
  override fun Content() {
    TabScreenRoot {
      HeaderTitle("You")
      ScreenContainer {
        AppButton(title = "Sign Out", onClick = {})
      }
    }
  }
}
