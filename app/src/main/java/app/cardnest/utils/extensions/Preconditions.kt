package app.cardnest.utils.extensions

import kotlin.IllegalStateException

/**
 * Throws an [IllegalStateException] with the result of calling [lazyMessage]  if [this] is null. Otherwise
 * returns the not null [this].
 */
fun <T> T?.checkNotNull(lazyMessage: () -> String): T {
  if (this == null) {
    throw IllegalStateException(lazyMessage())
  }

  return this
}

/**
 * Throws an [IllegalArgumentException] with the result of calling [lazyMessage] if [this] is null. Otherwise
 * returns the not null [this].
 */
fun <T> T?.requireNotNull(lazyMessage: () -> String): T {
  if (this == null) {
    throw IllegalArgumentException(lazyMessage())
  }

  return this
}
