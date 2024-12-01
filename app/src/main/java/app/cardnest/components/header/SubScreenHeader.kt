package app.cardnest.components.header

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.data.connectionState
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_10
import app.cardnest.utils.extensions.collectValue
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun SubScreenHeader(
  title: String,

  leftIconLabel: String = "Back",

  rightButtonLabel: String? = null,
  rightButtonIcon: Painter? = null,
  onRightButtonClick: () -> Unit = {},
  disableIfOffline: Boolean = false,
) {
  val ctx = LocalContext.current
  val navigator = LocalNavigator.currentOrThrow

  val isDisabledOffline = disableIfOffline && connectionState.collectValue().shouldWrite.not()

  fun onRightButtonClickWithOfflineCheck() {
    if (isDisabledOffline) {
      Toast.makeText(ctx, "You are offline", Toast.LENGTH_SHORT).show()
    } else {
      onRightButtonClick()
    }
  }

  Column(Modifier.statusBarsPadding()) {
    Box(Modifier.height(48.dp).fillMaxWidth()) {
      AppText(title, Modifier.align(Alignment.Center), color = TH_WHITE, weight = FontWeight.Bold)
      Row(Modifier.fillMaxSize(), Arrangement.SpaceBetween) {
        Box {
          if (navigator.canPop) {
            Row(Modifier.fillMaxHeight().clickable(onClick = navigator::pop).padding(start = 16.dp, end = 16.dp)) {
              Icon(
                painter = painterResource(id = R.drawable.tabler__chevron_left),
                contentDescription = null,
                modifier = Modifier.size(20.dp).align(Alignment.CenterVertically).padding(top = 1.dp),
                tint = TH_SKY,
              )

              Spacer(Modifier.size(4.dp))
              AppText(leftIconLabel, Modifier.align(Alignment.CenterVertically), color = TH_SKY)
            }
          }
        }

        Row(Modifier.fillMaxHeight().clickable(onClick = ::onRightButtonClickWithOfflineCheck).padding(start = 16.dp, end = 20.dp)) {
          if (rightButtonIcon != null) {
            Icon(
              painter = rightButtonIcon,
              contentDescription = null,
              tint = TH_SKY,
              modifier = Modifier.size(18.dp).align(Alignment.CenterVertically)
            )
          }

          Spacer(Modifier.size(2.dp))

          if (rightButtonLabel != null) {
            AppText(text = rightButtonLabel, Modifier.align(Alignment.CenterVertically), color = TH_SKY)
          }
        }
      }
    }

    HorizontalDivider(thickness = 0.5.dp, color = TH_WHITE_10)
  }
}
