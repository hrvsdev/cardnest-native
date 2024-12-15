package app.cardnest.utils.extensions

import android.util.Log
import app.cardnest.components.toast.AppToast
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException

fun Throwable.toast() {
  val toastMessage = when {
    this is TimeoutCancellationException || cause is TimeoutCancellationException -> "You seems to be offline, please check your internet connection and try again."
    this is CancellationException || cause is CancellationException -> null
    else -> message
  }

  if (toastMessage != null) {
    AppToast.error(toastMessage)
  }
}

fun Throwable.log(tag: String) {
  message?.let { Log.e(tag, it, this) }
}

fun Throwable.toastAndLog(tag: String) {
  toast()
  log(tag)
}
