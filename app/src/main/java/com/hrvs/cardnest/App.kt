package com.hrvs.cardnest

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hrvs.cardnest.components.CardPreview
import com.hrvs.cardnest.ui.theme.CardNestTheme
import com.hrvs.cardnest.ui.theme.TH_BLACK
import com.hrvs.cardnest.ui.theme.TH_WHITE
import com.hrvs.cardnest.ui.theme.TH_WHITE_07
import com.hrvs.cardnest.ui.theme.TH_WHITE_10
import com.hrvs.cardnest.ui.theme.TH_WHITE_60

@Preview
@Composable
fun App() {
  val (search, onSearchChange) = remember { mutableStateOf("") }

  CardNestTheme {
    Surface(Modifier.fillMaxSize(), color = TH_BLACK) {
      Column {
        Box(Modifier.padding(top = 32.dp, start = 18.dp, end = 16.dp)) {
          Text(
            text = "Home",
            color = TH_WHITE,
            fontSize = 28.sp,
            fontWeight = FontWeight(600),
            lineHeight = 34.sp
          )
        }
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
        Column(Modifier.padding(16.dp), Arrangement.spacedBy(16.dp)) {
          CardPreview(cardNumber = "6383 8737 7637 5373", expiry = "08/30")
        }
      }
    }
  }
}
