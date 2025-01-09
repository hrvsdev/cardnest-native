package app.cardnest.screens.user

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.containers.TabScreenRoot
import app.cardnest.components.header.HeaderTitle
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.screens.NoTransition
import app.cardnest.screens.user.account.AccountScreen
import app.cardnest.screens.user.manage_data.ManageDataScreen
import app.cardnest.screens.user.security.SecurityScreen
import app.cardnest.screens.user.user_interface.UserInterfaceScreen
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition

@Suppress("JavaIoSerializableObjectMustHaveReadResolve")
@OptIn(ExperimentalVoyagerApi::class)
object UserScreen : Screen, ScreenTransition by NoTransition() {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    TabScreenRoot {
      HeaderTitle("You")
      ScreenContainer(24.dp) {
        SettingsGroup("User Profile") {
          SettingsButton(
            title = "Account",
            icon = painterResource(R.drawable.tabler__user_circle),
            isFirst = true,
            isLast = true,
            onClick = { navigator.push(AccountScreen()) }
          )
        }

        SettingsGroup("App Settings") {
          SettingsButton(
            title = "Security",
            icon = painterResource(R.drawable.tabler__password_fingerprint),
            isFirst = true,
            onClick = { navigator.push(SecurityScreen()) }
          )
          SettingsButton(
            title = "User Interface",
            icon = painterResource(R.drawable.tabler__moon_stars),
            isLast = true,
            onClick = { navigator.push(UserInterfaceScreen()) }
          )
        }

        SettingsGroup("Data") {
          SettingsButton(
            title = "Data Management",
            icon = painterResource(R.drawable.tabler__database),
            isFirst = true,
            isLast = true,
            onClick = { navigator.push(ManageDataScreen()) }
          )
        }
      }
    }
  }
}
