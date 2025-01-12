package app.cardnest.screens.user.app_info.updates

sealed class UpdatesState {
  object Idle : UpdatesState()
  object Checking : UpdatesState()
  object NoUpdate : UpdatesState()
  data class UpdateAvailable(val version: String, val downloadUrl: String) : UpdatesState()
}
