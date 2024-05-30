package com.hrvs.cardnest.components.containers

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.components.header.SubScreenHeader
import com.hrvs.cardnest.ui.theme.TH_BLACK

val ScreenModifier = Modifier
  .fillMaxHeight()
  .background(TH_BLACK)

@Composable
fun TabScreenRoot(content: @Composable () -> Unit) {
  Column(ScreenModifier.verticalScroll(ScrollState(0), true)) {
    content()
  }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SubScreenRoot(
  title: String,

  leftIconLabel: String? = null,

  rightButtonLabel: String? = null,
  rightButtonIcon: ImageVector? = null,
  onRightButtonClick: () -> Unit = {},

  spacedBy: Dp? = null,

  content: @Composable () -> Unit
) {
  LazyColumn(ScreenModifier) {
    stickyHeader {
      SubScreenHeader(title, leftIconLabel, rightButtonLabel, rightButtonIcon, onRightButtonClick)
    }

    item {
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
