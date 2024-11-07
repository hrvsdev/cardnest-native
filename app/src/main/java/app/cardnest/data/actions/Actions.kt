package app.cardnest.data.actions

typealias Action = () -> Unit
typealias SuspendAction = suspend () -> Unit

object Actions {
  private var _afterPinCreated: Action = { }
  private var _afterPinVerified: SuspendAction = { }
  private var _onBottomSheetConfirm: Action = { }

  val afterPinCreated get() = _afterPinCreated
  val afterPinVerified get() = _afterPinVerified
  val onBottomSheetConfirm get() = _onBottomSheetConfirm

  fun setAfterPinCreated(action: Action) {
    _afterPinCreated = action
  }

  fun setAfterPinVerified(action: SuspendAction) {
    _afterPinVerified = action
  }

  fun setOnBottomSheetConfirm(action: Action) {
    _onBottomSheetConfirm = action
  }
}
