package app.cardnest.utils.extensions

import android.util.Log
import app.cardnest.components.toast.AppToast

fun Exception.toast() {
  message?.let { AppToast.error(it) }
}

fun Exception.log(tag: String) {
  Log.e(null, message, this.cause)
}

fun Exception.toastAndLog(tag: String) {
  toast()
  log(tag)
}
