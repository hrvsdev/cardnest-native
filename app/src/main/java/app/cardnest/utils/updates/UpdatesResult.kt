package app.cardnest.utils.updates

sealed class UpdatesResult {
  object NoUpdate : UpdatesResult()
  class UpdateAvailable(val version: String, val downloadUrl: String) : UpdatesResult()
}

