package app.cardnest.screens.home.card

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.card.CardDataManager
import app.cardnest.utils.extensions.toastAndLog
import cafe.adriel.voyager.navigator.Navigator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardViewModel(private val dataManager: CardDataManager, private val navigator: Navigator) : ViewModel() {
  var isDeleting by mutableStateOf(false)
    private set

  fun deleteCard(id: String) {
    if (isDeleting) return

    viewModelScope.launch(Dispatchers.IO) {
      delay(200)

      val isDeletingJob = launch {
        delay(200)
        isDeleting = true
      }

      try {
        dataManager.deleteCard(id)
        navigator.pop()
      } catch (e: Exception) {
        e.toastAndLog("CardViewModel")
      } finally {
        isDeletingJob.cancel()
        isDeleting = false
      }
    }
  }
}
