package app.cardnest.screens.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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

    val vm = koinViewModel<HomeViewModel>()

    val cardRecordList = vm.cardRecordList.collectAsStateWithLifecycle().value
    val filteredCardIds = vm.filteredCardIds.collectAsStateWithLifecycle().value
    val maskCardNumber = vm.maskCardNumber.collectAsStateWithLifecycle().value

    val totalNoOfCards = cardRecordList.size
    val noOfResults = filteredCardIds.size

    TabScreenRoot {
      HeaderTitle("Home")
      HeaderSearch(vm.queryState, noOfResults, totalNoOfCards)
      ScreenContainer {
        cardRecordList.forEachIndexed { index, it ->
          AnimatedVisibility(filteredCardIds.contains(it.id)) {
            Column {
              Box(Modifier.clickable { navigator.push(CardViewScreen(it.id)) }) {
                CardPreview(it.plainData, maskCardNumber = maskCardNumber)
              }

              Spacer(Modifier.size(16.dp))
            }
          }
        }

        if (totalNoOfCards == 0) {
          Box(Modifier.fillMaxWidth(), Alignment.Center) {
            AppText("No cards found", Modifier.padding(top = 32.dp), color = TH_WHITE_60)
          }
        }
      }
    }
  }
}
