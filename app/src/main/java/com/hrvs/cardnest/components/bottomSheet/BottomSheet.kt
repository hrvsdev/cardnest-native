package com.hrvs.cardnest.components.bottomSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.hrvs.cardnest.components.containers.appGradient
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.AppTextSize
import com.hrvs.cardnest.ui.theme.TH_SKY_20
import com.hrvs.cardnest.ui.theme.TH_WHITE

@Composable
fun BottomSheet(content: @Composable () -> Unit) {
  Box(Modifier.background(TH_SKY_20, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))) {
    Column(
      Modifier
        .padding(top = 1.dp)
        .background(appGradient, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
        .padding(16.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      content()
    }
  }
}

@Composable
fun BottomSheetHeading(title: String) {
  AppText(
    text = title,
    size = AppTextSize.XL,
    weight = FontWeight.Bold,
    color = TH_WHITE,
  )
}

@Composable
fun BottomSheetDescription(description: String) {
  AppText(text = description)
}

@Composable
fun BottomSheetButtons(content: @Composable () -> Unit) {
  Column(Modifier.padding(top = 40.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
    content()
  }
}
