package app.cardnest.data.actions

object Actions {
  val afterPasswordVerified = SuspendedAction()

  val afterPinCreated = SuspendedAction()
  val afterPinVerified = SuspendedAction()

  val onBottomSheetConfirm = Action()
}

typealias ActionType = () -> Unit
typealias SuspendedActionType = suspend () -> Unit

class Action() {
  private var action: ActionType? = null

  operator fun invoke(clear: Boolean = true) {
    action?.invoke()

    if (clear) {
      action = null
    }
  }

  fun set(action: ActionType) {
    this.action = action
  }
}

class SuspendedAction() {
  var action: SuspendedActionType? = null
    private set

  suspend operator fun invoke(clear: Boolean = true) {
    action?.invoke()

    if (clear) {
      action = null
    }
  }

  fun set(action: SuspendedActionType) {
    this.action = action
  }
}
