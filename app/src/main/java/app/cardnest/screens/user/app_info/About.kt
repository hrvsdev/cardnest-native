package app.cardnest.screens.user.app_info

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.BuildConfig
import app.cardnest.R
import app.cardnest.components.containers.SubScreenRoot
import app.cardnest.components.settings.SettingsButton
import app.cardnest.components.settings.SettingsGroup
import app.cardnest.components.settings.SettingsItem
import cafe.adriel.voyager.core.screen.Screen

class AboutScreen : Screen {
  @Composable
  override fun Content() {
    val uriHandler = LocalUriHandler.current

    fun onOpenSourceCode() {
      val GITHUB_REPO = "https://github.com/hrvsdev/cardnest-native"
      uriHandler.openUri(GITHUB_REPO)
    }

    SubScreenRoot("About", backLabel = "Settings", spacedBy = 24.dp) {
      SettingsGroup("About App") {
        SettingsItem(
          title = "CardNest v${BuildConfig.VERSION_NAME}",
          icon = painterResource(R.drawable.tabler__info_circle),
          isFirst = true,
        )
        SettingsButton(
          title = "Source code",
          icon = painterResource(R.drawable.tabler__brand_github),
          isLast = true,
          isExternalLink = true,
          onClick = ::onOpenSourceCode
        )
      }
    }
  }
}
