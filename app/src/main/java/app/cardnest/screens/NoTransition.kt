package app.cardnest.screens

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import cafe.adriel.voyager.core.annotation.ExperimentalVoyagerApi
import cafe.adriel.voyager.core.stack.StackEvent
import cafe.adriel.voyager.transitions.ScreenTransition

@OptIn(ExperimentalVoyagerApi::class)
class NoTransition : ScreenTransition {
  override fun enter(lastEvent: StackEvent): EnterTransition {
    return EnterTransition.Companion.None
  }

  override fun exit(lastEvent: StackEvent): ExitTransition {
    return ExitTransition.Companion.None
  }
}
