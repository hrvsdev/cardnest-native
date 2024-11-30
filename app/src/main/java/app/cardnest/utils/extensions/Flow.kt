@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package app.cardnest.utils.extensions

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

context(ViewModel)
fun <T> Flow<T>.stateInViewModel(initialValue: T, started: SharingStarted = SharingStarted.WhileSubscribed(5000)): StateFlow<T> {
  return stateIn(scope = viewModelScope, started = started, initialValue = initialValue)
}

context(ViewModel)
fun <T1, T2, R> combineStateInViewModel(
  flow: Flow<T1>,
  flow2: Flow<T2>,
  initialValue: R,
  started: SharingStarted = SharingStarted.WhileSubscribed(5000),
  transform: suspend (T1, T2) -> R
): StateFlow<R> {
  return combine(flow, flow2, transform).stateInViewModel(initialValue, started)
}

context(ViewModel)
fun <T1, T2, T3, R> combineStateInViewModel(
  flow: Flow<T1>,
  flow2: Flow<T2>,
  flow3: Flow<T3>,
  initialValue: R,
  started: SharingStarted = SharingStarted.WhileSubscribed(5000),
  transform: suspend (T1, T2, T3) -> R
): StateFlow<R> {
  return combine(flow, flow2, flow3, transform).stateInViewModel(initialValue, started)
}

suspend fun <T1, T2, R> combineCollectLatest(
  flow: Flow<T1>,
  flow2: Flow<T2>,
  transform: suspend (T1, T2) -> R
) {
  return combine(flow, flow2, transform).collectLatest {}
}

@Composable
fun <T> StateFlow<T>.collectValue(): T {
  return collectAsStateWithLifecycle().value
}

