package app.cardnest.utils.extensions

import app.cardnest.data.actions.Actions.setOnBottomSheetConfirm
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.bottomSheet.BottomSheetNavigator

fun BottomSheetNavigator.open(screen: Screen, onConfirm: () -> Unit) {
  setOnBottomSheetConfirm(onConfirm)
  show(screen)
}
