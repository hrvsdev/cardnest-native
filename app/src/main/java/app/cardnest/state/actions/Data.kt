package app.cardnest.state.actions

import androidx.lifecycle.ViewModel

typealias Action = () -> Unit
typealias SuspendAction = suspend () -> Unit

class ActionsViewModel : ViewModel() {
  private var _afterPinCreated: Action = { }
  private var _afterPinVerified: SuspendAction = { }

  val afterPinCreated get() = _afterPinCreated
  val afterPinVerified get() = _afterPinVerified

  fun setAfterPinCreated(action: Action) {
    _afterPinCreated = action
  }

  fun setAfterPinVerified(action: SuspendAction) {
    _afterPinVerified = action
  }
}
