package com.hrvs.cardnest.screens.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import com.hrvs.cardnest.R
import com.hrvs.cardnest.components.button.AppButton
import com.hrvs.cardnest.components.containers.ScreenContainer
import com.hrvs.cardnest.components.containers.TabScreenRoot
import com.hrvs.cardnest.components.header.HeaderTitle
import com.hrvs.cardnest.ui.theme.AppText
import com.hrvs.cardnest.ui.theme.TH_WHITE_70

class AddCardScreen : Screen {
  @Composable
  override fun Content() {
    TabScreenRoot {
      HeaderTitle("Add Card")
      ScreenContainer {
        Column(Modifier.weight(1f), Arrangement.Center, Alignment.CenterHorizontally) {
          Icon(
            painter = painterResource(R.drawable.tabler__circle_plus),
            contentDescription = null,
            modifier = Modifier.size(128.dp),
            tint = TH_WHITE_70
          )

          Spacer(modifier = Modifier.height(16.dp))

          AppText("Add a new card to add to your collection.", align = TextAlign.Center)
          AppText(
            text = "You can't scan cards yet but you can add them manually.",
            align = TextAlign.Center
          )
        }
        AppButton(title = "Add Card", onClick = { /*TODO*/ })
      }
    }
  }
}
