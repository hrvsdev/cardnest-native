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
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

val defaultSharing get() = SharingStarted.WhileSubscribed(5000)

fun <T> Flow<T>.exists(): Flow<Boolean> {
  return map { it != null }
}

fun <T> Flow<T>.zipWithNext(): Flow<Pair<T, T>> = flow {
  var last: Any? = INTERNAL_NULL_VALUE

  collect {
    if (last !== INTERNAL_NULL_VALUE) {
      @Suppress("UNCHECKED_CAST") emit(last as T to it)
    }

    last = it
  }
}

context(ViewModel)
fun <T> Flow<T>.stateInViewModel(initialValue: T, started: SharingStarted = defaultSharing): StateFlow<T> {
  return stateIn(scope = viewModelScope, started = started, initialValue = initialValue)
}

context(ViewModel)
fun <T> Flow<T>.existsStateInViewModel(initialValue: Boolean = false, started: SharingStarted = defaultSharing): StateFlow<Boolean> {
  return exists().stateInViewModel(initialValue, started)
}

context(ViewModel)
fun <T1, T2, R> combineStateInViewModel(
  flow: Flow<T1>,
  flow2: Flow<T2>,
  initialValue: R,
  started: SharingStarted = defaultSharing,
  transform: suspend (T1, T2) -> R
): StateFlow<R> {
  return combine(flow, flow2, transform).stateInViewModel(initialValue, started)
}

suspend fun <T1, T2, R> combineCollectLatest(flow: Flow<T1>, flow2: Flow<T2>, transform: suspend (T1, T2) -> R) {
  return combine(flow, flow2, transform).collectLatest {}
}

@Composable
fun <T> StateFlow<T>.collectValue(): T {
  return collectAsStateWithLifecycle().value
}

@Suppress("ClassName")
private object INTERNAL_NULL_VALUE
