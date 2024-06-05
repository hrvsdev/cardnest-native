package com.hrvs.cardnest.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.components.header.SubScreenHeader
import com.hrvs.cardnest.ui.theme.TH_BLACK

val ScreenModifier = Modifier
  .fillMaxHeight()
  .background(TH_BLACK)

@Composable
fun TabScreenRoot(content: @Composable () -> Unit) {
  Column(ScreenModifier) {
    content()
  }
}

@Composable
fun SubScreenRoot(
  title: String,

  leftIconLabel: String? = null,

  rightButtonLabel: String? = null,
  rightButtonIcon: Painter? = null,
  onRightButtonClick: () -> Unit = {},

  spacedBy: Dp? = null,

  content: @Composable () -> Unit
) {
  Column(ScreenModifier) {
    SubScreenHeader(title, leftIconLabel, rightButtonLabel, rightButtonIcon, onRightButtonClick)
    Box(Modifier.verticalScroll(rememberScrollState())) {
      ScreenContainer(spacedBy, content)
    }
  }
}


@Composable
fun ScreenContainer(spacedBy: Dp? = null, content: @Composable () -> Unit) {
  Column(
    modifier = Modifier.padding(16.dp),
    verticalArrangement = if (spacedBy != null) Arrangement.spacedBy(spacedBy) else Arrangement.Top
  ) {
    content()
  }
}
