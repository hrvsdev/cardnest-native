package app.cardnest

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

val LocalTabBarVisibility = compositionLocalOf { mutableStateOf(true) }

@Composable
fun CompositionProvider(content: @Composable () -> Unit) {
  val showTabBar = remember { mutableStateOf(true) }

  CompositionLocalProvider(
    LocalTabBarVisibility provides showTabBar,
    content = content,
  )
}
