@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package app.cardnest.utils.extensions

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

context(ViewModel)
fun <T> Flow<T>.stateIn(initialValue: T, started: SharingStarted = SharingStarted.WhileSubscribed(5000)): StateFlow<T> {
  return stateIn(scope = viewModelScope, started = started, initialValue = initialValue)
}

@Composable
fun <T> StateFlow<T>.collectValue(): T {
  return collectAsStateWithLifecycle().value
}

