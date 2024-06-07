package com.hrvs.cardnest.components.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.AppTextSize
import com.hrvs.cardnest.ui.theme.TH_WHITE

@Composable
fun HeaderTitle(title: String) {
  Box(Modifier.padding(top = 32.dp, start = 18.dp, end = 16.dp).statusBarsPadding()) {
    AppText(
      text = title,
      color = TH_WHITE,
      size = AppTextSize.HEADING,
      weight = FontWeight.Bold
    )
  }
}
