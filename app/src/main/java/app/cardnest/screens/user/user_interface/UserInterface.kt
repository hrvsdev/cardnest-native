package app.cardnest.screens.user.user_interface

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.components.settings.SettingsSwitch
import app.cardnest.utils.extensions.collectValue
import cafe.adriel.voyager.core.screen.Screen
import org.koin.androidx.compose.koinViewModel

class UserInterfaceScreen : Screen {
  @Composable
  override fun Content() {
    val vm = koinViewModel<UserInterfaceViewModel>()

    val maskCardNumber = vm.maskCardNumber.collectValue()

    SubScreenRoot("User Interface", backLabel = "Settings", spacedBy = 24.dp) {
      SettingsGroup("Card", MASK_CARD_DESC) {
        SettingsSwitch(
          title = "Mask card number",
          icon = painterResource(R.drawable.tabler__password),
          checked = maskCardNumber,
          onCheckedChange = vm::onMaskCardNumberChange,
          isFirst = true,
          isLast = true,
        )
      }
    }
  }
}

const val MASK_CARD_DESC = "Mask the card number on the card preview on home screen."
