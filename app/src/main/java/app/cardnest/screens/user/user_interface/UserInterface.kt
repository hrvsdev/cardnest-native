package app.cardnest.screens.user.user_interface

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.components.settings.SettingsSwitch
import cafe.adriel.voyager.core.screen.Screen

class UserInterfaceScreen : Screen {
  @Composable
  override fun Content() {
    var maskCard by remember { mutableStateOf(false) }

    SubScreenRoot("User Interface", leftIconLabel = "Settings", spacedBy = 24.dp) {
      SettingsGroup("Card", MASK_CARD_DESC) {
        SettingsSwitch(
          title = "Mask card number",
          icon = painterResource(R.drawable.tabler__password),
          checked = maskCard,
          onCheckedChange = { maskCard = it },
          isFirst = true,
          isLast = true,
        )
      }
    }
  }
}

const val MASK_CARD_DESC = "Mask the card number on the card preview on home screen page.";
