package app.cardnest.data

import android.util.Log
import app.cardnest.components.toast.AppToast

class AppException(override val message: String? = null, override val cause: Throwable? = null) : Exception(message, cause) {
  fun toast() {
    if (message != null) AppToast.error(message)
  }

  fun log(tag: String) {
    if (message != null || cause != null) Log.e(tag, message, cause)
  }

  fun toastAndLog(tag: String) {
    toast()
    log(tag)
  }
}

