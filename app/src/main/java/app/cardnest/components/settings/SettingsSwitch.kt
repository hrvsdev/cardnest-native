package app.cardnest.components.settings

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.components.core.AppSwitch
import app.cardnest.data.connectionState

@Composable
fun SettingsSwitch(
  title: String,
  icon: Painter,
  checked: Boolean,
  onCheckedChange: (checked: Boolean) -> Unit,
  isFirst: Boolean = false,
  isLast: Boolean = false,
  disableIfOffline: Boolean = false,
) {
  val ctx = LocalContext.current
  val isDisabledOffline = disableIfOffline && connectionState.collectAsStateWithLifecycle().value.shouldWrite.not()

  fun onCheckedChangeWithOfflineCheck(checked: Boolean) {
    if (isDisabledOffline) {
      Toast.makeText(ctx, "You are offline", Toast.LENGTH_SHORT).show()
    } else {
      onCheckedChange(checked)
    }
  }

  SettingsItem(
    title = title,
    icon = icon,
    isFirst = isFirst,
    isLast = isLast,
    isDisabled = isDisabledOffline,
    rightContent = {
      AppSwitch(checked, ::onCheckedChangeWithOfflineCheck, isDisabledOffline)
    }
  )
}
