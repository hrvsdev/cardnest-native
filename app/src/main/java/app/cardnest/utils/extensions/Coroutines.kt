package app.cardnest.utils.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun <T> withDefault(block: suspend CoroutineScope.() -> T): T {
  return withContext(Dispatchers.Default, block)
}

suspend fun <T> withIO(block: suspend CoroutineScope.() -> T): T {
  return withContext(Dispatchers.IO, block)
}

suspend fun <T> withMain(block: suspend CoroutineScope.() -> T): T {
  return withContext(Dispatchers.Main, block)
}
