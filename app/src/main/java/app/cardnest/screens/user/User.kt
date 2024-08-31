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
import app.cardnest.screens.user.security.SecurityScreen
import app.cardnest.screens.user.user_interface.UserInterfaceScreen
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Suppress("JavaIoSerializableObjectMustHaveReadResolve")
@OptIn(ExperimentalVoyagerApi::class)
object UserScreen : Screen, ScreenTransition by NoTransition() {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<UserViewModel> { parametersOf(navigator, bottomSheetNavigator) }

    fun onDeleteAllCardsClick() {
      bottomSheetNavigator.show(DeleteDataBottomSheetScreen(vm::onDeleteAllCards, bottomSheetNavigator::hide))
    }

    TabScreenRoot {
      HeaderTitle("You")
      ScreenContainer(24.dp) {
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

        SettingsGroup("Danger Zone", DELETE_DESCRIPTION) {
          SettingsButton(
            title = "Delete all data",
            icon = painterResource(R.drawable.tabler__trash),
            isDanger = true,
            isFirst = true,
            isLast = true,
            onClick = ::onDeleteAllCardsClick
          )
        }
      }
    }
  }
}

const val DELETE_DESCRIPTION =
  "Deleting data will forever delete all data from all your devices and there is no way to recover it."

