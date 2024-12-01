package app.cardnest.components.settings

import android.widget.Toast
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.loader.LoadingIcon
import app.cardnest.data.connectionState
import app.cardnest.ui.theme.TH_RED_40
import app.cardnest.ui.theme.TH_RED_60
import app.cardnest.ui.theme.TH_WHITE_35
import app.cardnest.ui.theme.TH_WHITE_50
import app.cardnest.utils.extensions.collectValue

@Composable
fun SettingsButton(
  title: String,
  icon: Painter,
  isDanger: Boolean = false,
  isFirst: Boolean = false,
  isLast: Boolean = false,
  isLoading: Boolean = false,
  disableIfOffline: Boolean = false,
  onClick: () -> Unit,
) {
  val ctx = LocalContext.current
  val isDisabledOffline = disableIfOffline && connectionState.collectValue().shouldWrite.not()

  val isDisabled = isDisabledOffline || isLoading

  fun onButtonClick() {
    if (isDisabledOffline) {
      Toast.makeText(ctx, "You are offline", Toast.LENGTH_SHORT).show()
    } else {
      onClick()
    }
  }

  val color = if (isDanger) {
    if (isDisabled) TH_RED_40 else TH_RED_60
  } else {
    if (isDisabled) TH_WHITE_35 else TH_WHITE_50
  }

  SettingsItem(
    title = title,
    icon = icon,
    isDanger = isDanger,
    isFirst = isFirst,
    isLast = isLast,
    isDisabled = isDisabled,
    onClick = ::onButtonClick,
    rightContent = {
      if (isLoading) {
        LoadingIcon(color)
      } else {
        ChevronRightIcon(color)
      }
    }
  )
}

@Composable
private fun ChevronRightIcon(color: Color) {
  Icon(
    painter = painterResource(R.drawable.tabler__chevron_right),
    contentDescription = null,
    modifier = Modifier.size(20.dp),
    tint = color
  )
}
