package com.hrvs.cardnest.components.header

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_07
import com.hrvs.cardnest.ui.theme.TH_WHITE_10
import com.hrvs.cardnest.ui.theme.TH_WHITE_60

@Composable
fun HeaderSearch() {
  val (search, onSearchChange) = remember { mutableStateOf("") }

  Box(Modifier.padding(16.dp)) {
    TextField(
      singleLine = true,
      value = search,
      onValueChange = onSearchChange,
      placeholder = {
        Text(
          text = "Enter card number, bank or network",
          color = TH_WHITE_60,
          fontSize = 16.sp,
          lineHeight = 20.sp
        )
      },
      prefix = {
        Icon(
          imageVector = Icons.Outlined.Search,
          contentDescription = "Search",
          Modifier
            .padding(end = 8.dp)
            .size(28.dp),
          tint = TH_WHITE_60
        )
      },
      suffix = {
        if (search.isNotEmpty()) {
          IconButton(onClick = { onSearchChange("") }) {
            Icon(
              imageVector = Icons.Outlined.Clear,
              contentDescription = "Clear",
              tint = TH_WHITE_60
            )
          }
        }
      },
      textStyle = TextStyle(color = TH_WHITE, fontSize = 16.sp),
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(14.dp),
      colors = TextFieldDefaults.colors(
        unfocusedContainerColor = TH_WHITE_07,
        focusedContainerColor = TH_WHITE_10,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
      )
    )
  }
}
