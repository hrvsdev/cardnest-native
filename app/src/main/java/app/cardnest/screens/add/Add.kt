package app.cardnest.screens.add

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
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import app.cardnest.R
import app.cardnest.components.button.AppButton
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.containers.TabScreenRoot
import app.cardnest.components.header.HeaderTitle
import app.cardnest.screens.add.editor.AddCardEditorScreen
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.TH_WHITE_70

class AddCardScreen : Screen {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    TabScreenRoot {
      HeaderTitle("Add Card")
      ScreenContainer(modifier = Modifier.weight(1f)) {
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
        AppButton(title = "Add Card", onClick = { navigator.push(AddCardEditorScreen()) })
      }
    }
  }
}