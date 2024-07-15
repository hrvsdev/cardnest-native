package app.cardnest.components.tabs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import app.cardnest.R
import app.cardnest.screens.add.AddCardScreen
import app.cardnest.screens.home.HomeScreen
import app.cardnest.screens.user.UserScreen
import app.cardnest.ui.theme.TH_BLACK
import app.cardnest.ui.theme.TH_SKY
import app.cardnest.ui.theme.TH_WHITE_10
import app.cardnest.ui.theme.TH_WHITE_70
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

data class TabData(
  val index: Int,
  val title: String,
  val icon: Int,
  val screen: Screen,
)

val tabs = listOf(
  TabData(0, "Home", R.drawable.heroicons__home_solid, HomeScreen),
  TabData(1, "Add Card", R.drawable.heroicons__credit_card_solid, AddCardScreen),
  TabData(2, "User", R.drawable.heroicons__user_circle_solid, UserScreen),
)

@Composable
fun BoxScope.TabBar(show: Boolean) {
  val navigator = LocalNavigator.currentOrThrow
  val selectedIndex = tabs.indexOfLast { it.screen in navigator.items }

  fun onTabClick(screen: Screen, index: Int) {
    when {
      // Push the screen if the target tab index is higher
      selectedIndex < index -> navigator.push(screen)

      // Pop until the target tab if it's already in the stack
      selectedIndex > index && screen in navigator.items -> {
        navigator.popUntil { it == screen }
      }

      // Replace the current screen if the target tab is not in the stack
      selectedIndex > index -> navigator.replace(screen)
    }
  }

  Box(Modifier.align(Alignment.BottomCenter)) {
    AnimatedVisibility(show, enter = slideInVertically { it }, exit = slideOutVertically { it }) {
      Column(Modifier.background(TH_BLACK)) {
        HorizontalDivider(thickness = 0.5.dp, color = TH_WHITE_10)

        Row {
          tabs.forEach {
            TabButton(
              title = it.title,
              icon = painterResource(it.icon),
              onClick = { onTabClick(it.screen, it.index) },
              isSelected = it.index == selectedIndex,
            )
          }
        }
      }
    }
  }
}

@Composable
fun RowScope.TabButton(
  title: String,
  icon: Painter,
  onClick: () -> Unit,
  isSelected: Boolean,
) {
  Box(
    modifier = Modifier
      .weight(1f)
      .clickable(onClick = onClick)
      .padding(vertical = 16.dp),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      painter = icon,
      contentDescription = title,
      modifier = Modifier.size(24.dp),
      tint = if (isSelected) TH_SKY else TH_WHITE_70,
    )
  }
}
