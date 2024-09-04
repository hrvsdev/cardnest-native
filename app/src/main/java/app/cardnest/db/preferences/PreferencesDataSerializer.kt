package app.cardnest.db.preferences

import app.cardnest.data.preferences.Preferences
import app.cardnest.utils.serialization.DataSerializer

object PreferencesDataSerializer : DataSerializer<Preferences>(Preferences.serializer()) {
  override val defaultInstance: Preferences = Preferences()
}
