package app.cardnest.screens.user.manage_data

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.utils.extensions.open
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

class ManageDataScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    val vm = koinViewModel<ManageDataViewModel> { parametersOf(navigator, bottomSheetNavigator) }

    fun onDeleteAllCards() {
      bottomSheetNavigator.open(DeleteDataBottomSheetScreen()) {
        bottomSheetNavigator.hide()
        vm.onDeleteAllCards()
      }
    }

    SubScreenRoot("Data Management", backLabel = "Settings", spacedBy = 24.dp) {
      SettingsGroup("Danger Zone", DELETE_CARDS_DESC) {
        SettingsButton(
          title = "Delete all cards",
          icon = painterResource(R.drawable.tabler__trash),
          isDanger = true,
          isFirst = true,
          isLast = true,
          onClick = ::onDeleteAllCards
        )
      }
    }
  }
}

const val DELETE_CARDS_DESC = "Delete to remove your cards data from this device and the server forever."



