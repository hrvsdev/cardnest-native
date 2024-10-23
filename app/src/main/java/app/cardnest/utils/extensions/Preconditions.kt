package app.cardnest.utils.extensions

import app.cardnest.data.AppException

/**
 * Throws an [AppException] with the result of calling [lazyLabel] and adding message if [this] is null. Otherwise
 * returns the not null value.
 *
 */
fun <T> T?.checkNotNull(lazyLabel: () -> String): T {
  if (this == null) {
    val label = lazyLabel()
    val message = "$label must not be null"

    try {
      throw IllegalStateException(message)
    } catch (e: IllegalStateException) {
      throw AppException(message, e)
    }
  }
  return this
}

/**
 * Throws an [AppException] with the result of calling [lazyLabel] and adding message if [this] is null. Otherwise
 * returns the not null value.
 *
 */
fun <T> T?.requireNotNull(lazyLabel: () -> String): T {
  if (this == null) {
    val label = lazyLabel()
    val message = "$label is required"

    try {
      throw IllegalArgumentException(message)
    } catch (e: IllegalStateException) {
      throw AppException(message, e)
    }
  }
  return this
}
