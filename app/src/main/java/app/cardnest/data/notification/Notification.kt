package app.cardnest.data.notification

enum class Notification(val message: String, val theme: NotificationType) {
  CARD_DATA_CORRUPTED(
    message = "Cards data has been corrupted and is in a unrecoverable state. Click to delete all data and start fresh.",
    theme = NotificationType.ERROR
  ),

  AUTH_DATA_CORRUPTED(
    message = "Encryption Key or related auth data has been corrupted. Click to reset your PIN.",
    theme = NotificationType.ERROR
  ),

  AUTH_DATA_NOT_UPDATED(
    message = "Sync has been paused because your password was changed and not updated on this device. Click to re-login.",
    theme = NotificationType.INFO
  )
}

enum class NotificationType {
  INFO, ERROR
}
