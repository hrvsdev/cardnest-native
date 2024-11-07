package app.cardnest.utils.extensions

import android.util.Log
import app.cardnest.components.toast.AppToast

fun Throwable.toast() {
  message?.let { AppToast.error(it) }
}

fun Throwable.log(tag: String) {
  Log.e(tag, message, this)
}

fun Throwable.toastAndLog(tag: String) {
  toast()
  log(tag)
}
