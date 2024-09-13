package app.cardnest.screens.user.account

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.data.userState
import app.cardnest.firebase.auth.FirebaseUserManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AccountViewModel(private val userManager: FirebaseUserManager) : ViewModel() {
  val isLoading = mutableStateOf(false)
  val isSignedIn = userState.map { it != null }.stateIn(
    scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000), initialValue = false
  )

  fun signInWithGoogle(ctx: Context) {
    isLoading.value = true
    viewModelScope.launch {
      userManager.signInWithGoogle(ctx) {
        isLoading.value = false
      }
    }
  }

  fun signOut() {
    userManager.signOut()
  }
}

