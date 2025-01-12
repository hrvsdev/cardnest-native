package app.cardnest.screens.user.app_info.updates

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.buildAnnotatedString
import app.cardnest.components.bottomSheet.BottomSheet
import app.cardnest.components.bottomSheet.BottomSheetButtons
import app.cardnest.components.bottomSheet.BottomSheetCancelButton
import app.cardnest.components.bottomSheet.BottomSheetDescription
import app.cardnest.components.bottomSheet.BottomSheetHeading
import app.cardnest.components.button.AppButton
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.utils.extensions.appendWithEmphasis
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.LocalBottomSheetNavigator

data class UpdatesBottomSheetScreen(private val latestVersion: String, private val downloadUrl: String) : Screen {
  @Composable
  override fun Content() {
    val uriHandler = LocalUriHandler.current
    val bottomSheetNavigator = LocalBottomSheetNavigator.current

    fun onDownload() {
      bottomSheetNavigator.hide()
      uriHandler.openUri(downloadUrl)
    }

    BottomSheet {
      BottomSheetHeading("Update CardNest")

      BottomSheetDescription(buildAnnotatedString {
        append("A new version ")
        appendWithEmphasis("$latestVersion ", color = TH_SKY)
        append("is available.")
      })

      BottomSheetDescription("Update now to get the latest features, bug fixes and improvements.")

      BottomSheetButtons {
        BottomSheetCancelButton()
        AppButton("Download", onClick = ::onDownload)
      }
    }
  }
}
