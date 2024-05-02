package com.hrvs.cardnest.ui.theme

import android.app.Activity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

@Composable
fun CardNestTheme(content: @Composable () -> Unit) {
  val view = LocalView.current
  if (!view.isInEditMode) {
    SideEffect {
      val window = (view.context as Activity).window
      window.statusBarColor = TH_BLACK.toArgb()
      WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
    }
  }

  MaterialTheme(
    content = content
  )
}

@Composable
fun TabScreenRoot(content: @Composable () -> Unit) {
  CardNestTheme {
    Surface(Modifier.fillMaxSize(), color = TH_BLACK) {
      Column {
        content()
      }
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
