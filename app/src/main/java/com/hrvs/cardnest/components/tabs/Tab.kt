package com.hrvs.cardnest.components.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import cafe.adriel.voyager.navigator.tab.LocalTabNavigator
import cafe.adriel.voyager.navigator.tab.Tab
import com.hrvs.cardnest.AddCardTab
import com.hrvs.cardnest.HomeTab
import com.hrvs.cardnest.R
import com.hrvs.cardnest.UserTab
import com.hrvs.cardnest.ui.theme.TH_BLACK
import com.hrvs.cardnest.ui.theme.TH_SKY
import com.hrvs.cardnest.ui.theme.TH_WHITE_10
import com.hrvs.cardnest.ui.theme.TH_WHITE_70

@Composable
fun TabBar() {
  Column(Modifier.background(TH_BLACK)) {
    HorizontalDivider(thickness = 0.5.dp, color = TH_WHITE_10)
    Row {
      TabButton(HomeTab, painterResource(R.drawable.heroicons__home_solid))
      TabButton(AddCardTab, painterResource(R.drawable.heroicons__credit_card_solid))
      TabButton(UserTab, painterResource(R.drawable.heroicons__user_circle_solid))
    }
  }
}

@Composable
fun RowScope.TabButton(
  tab: Tab,
  icon: Painter,
) {
  val tabNavigator = LocalTabNavigator.current

  Box(
    modifier = Modifier
      .weight(1f)
      .clickable(onClick = { tabNavigator.current = tab })
      .padding(vertical = 16.dp),
    contentAlignment = Alignment.Center,
  ) {
    Icon(
      painter = icon,
      contentDescription = tab.options.title,
      modifier = Modifier.size(24.dp),
      tint = if (tabNavigator.current.key == tab.key) TH_SKY else TH_WHITE_70,
    )
  }
}
