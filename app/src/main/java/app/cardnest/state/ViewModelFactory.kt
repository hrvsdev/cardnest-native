package app.cardnest.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

fun <VM : ViewModel> appViewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
  return object : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      @Suppress("UNCHECKED_CAST")
      return initializer() as T
    }
  }
}
