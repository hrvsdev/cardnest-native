package app.cardnest.screens.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.components.card.CardPreview
import app.cardnest.components.containers.ScreenContainer
import app.cardnest.components.containers.TabScreenRoot
import app.cardnest.components.header.HeaderSearch
import app.cardnest.components.header.HeaderTitle
import app.cardnest.screens.NoTransition
import app.cardnest.screens.home.card.CardViewScreen
import app.cardnest.state.card.CardsDataViewModel
import app.cardnest.ui.theme.AppText
import app.cardnest.ui.theme.TH_WHITE_60
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.transitions.ScreenTransition
import org.koin.androidx.compose.koinViewModel

@Suppress("JavaIoSerializableObjectMustHaveReadResolve")
@OptIn(ExperimentalVoyagerApi::class)
object HomeScreen : Screen, ScreenTransition by NoTransition() {
  @Composable
  override fun Content() {
    val navigator = LocalNavigator.currentOrThrow

    val cards = koinViewModel<CardsDataViewModel>().state.collectAsStateWithLifecycle().value

    TabScreenRoot {
      HeaderTitle("Home")
      HeaderSearch()
      ScreenContainer(16.dp) {
        cards.forEach {
          Box(Modifier.clickable { navigator.push(CardViewScreen(it)) }) {
            CardPreview(it.plainData)
          }
        }

        if (cards.isEmpty()) {
          Box(Modifier.fillMaxWidth(), Alignment.Center) {
            AppText("No cards found", Modifier.padding(top = 32.dp), color = TH_WHITE_60)
          }
        }
      }
    }
  }
}
