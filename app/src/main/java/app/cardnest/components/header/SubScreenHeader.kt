package app.cardnest.components.header

import androidx.compose.animation.animateColorAsState
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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.components.loader.LoadingIcon
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_SKY_50
import app.cardnest.ui.theme.TH_WHITE
import app.cardnest.ui.theme.TH_WHITE_10
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

@Composable
fun SubScreenHeader(title: String, backLabel: String = "Back") {
  SubScreenHeader(title, { HeaderBackButton(backLabel) })
}

@Composable
fun SubScreenHeader(title: String, backLabel: String = "Back", actionButton: @Composable () -> Unit) {
  SubScreenHeader(title, { HeaderBackButton(backLabel) }, actionButton)
}

@Composable
fun SubScreenHeader(title: String, backButton: @Composable () -> Unit, actionButton: @Composable () -> Unit = {}) {
  Column(Modifier.statusBarsPadding()) {
    Box(Modifier.height(48.dp).fillMaxWidth()) {
      AppText(title, Modifier.align(Alignment.Center), color = TH_WHITE, weight = FontWeight.Bold)
      Row(Modifier.fillMaxSize(), Arrangement.SpaceBetween) {
        backButton()
        actionButton()
      }
    }

    HorizontalDivider(thickness = 0.5.dp, color = TH_WHITE_10)
  }
}

@Composable
fun HeaderBackButton(label: String) {
  val navigator = LocalNavigator.currentOrThrow

  Box(Modifier.fillMaxHeight().clickable(onClick = navigator::pop).padding(start = 16.dp, end = 16.dp)) {
    if (navigator.canPop) {
      Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
        Icon(
          painter = painterResource(id = R.drawable.tabler__chevron_left),
          contentDescription = null,
          modifier = Modifier.size(20.dp).padding(top = 1.dp),
          tint = TH_SKY,
        )

        Spacer(Modifier.size(4.dp))
        AppText(label, color = TH_SKY)
      }
    }
  }
}

@Composable
fun HeaderActionButton(label: String, icon: Painter? = null, isLoading: Boolean = false, onClick: () -> Unit) {
  val color by animateColorAsState(if (isLoading) TH_SKY_50 else TH_SKY)

  Box(Modifier.fillMaxHeight().clickable(onClick = onClick, enabled = isLoading.not()).padding(start = 16.dp, end = 20.dp)) {
    Row(Modifier.fillMaxHeight(), verticalAlignment = Alignment.CenterVertically) {
      when {
        isLoading -> LoadingIcon(color, size = 16.dp)
        icon != null -> Icon(
          painter = icon,
          contentDescription = null,
          tint = color,
          modifier = Modifier.size(18.dp)
        )
      }

      Spacer(Modifier.size(if (isLoading) 4.dp else if (icon != null) 2.dp else 0.dp))
      AppText(text = label, color = color)
    }
  }
}
