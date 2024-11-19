package app.cardnest.utils.extensions

import app.cardnest.data.actions.Actions.onBottomSheetConfirm
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator

fun BottomSheetNavigator.open(screen: Screen, onConfirm: () -> Unit) {
  onBottomSheetConfirm.set(onConfirm)
  show(screen)
}
