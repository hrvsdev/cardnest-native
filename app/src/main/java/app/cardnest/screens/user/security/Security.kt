package app.cardnest.screens.user.security

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.dp
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsGroup
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import app.cardnest.components.settings.SettingsButton
import app.cardnest.R
import app.cardnest.screens.pin.create.CreatePinScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

class SecurityScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    var hasCreatedPin by remember { mutableStateOf(false) }

    SubScreenRoot("Security", leftIconLabel = "Settings", spacedBy = 24.dp) {
      SettingsGroup("Password", if (hasCreatedPin) null else CREATE_PASSWORD_DESC) {
        SettingsButton(
          title = if (hasCreatedPin) "Change password" else "Create password",
          icon = painterResource(R.drawable.tabler__password_mobile_phone),
          onClick = { navigator.push(CreatePinScreen()) },
          isFirst = true,
          isLast = true,
        )
      }

      AnimatedVisibility(hasCreatedPin) {
        SettingsGroup("Danger zone", REMOVE_PASSWORD_DESC) {
          SettingsButton(
            title = "Remove app password",
            icon = painterResource(R.drawable.tabler__lock_open_off),
            onClick = {},
            isFirst = true,
            isLast = true,
          )
        }
      }
    }
  }
}

const val CREATE_PASSWORD_DESC =
  "Creating a password will make your data private and secure. You will need to enter the password every time you open the app.";

const val REMOVE_PASSWORD_DESC =
  "Removing your app password will make all your data accessible to anyone who has access to your device.";
