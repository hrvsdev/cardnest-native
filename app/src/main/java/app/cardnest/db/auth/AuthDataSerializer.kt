package app.cardnest.db.auth

import app.cardnest.data.auth.AuthData
import app.cardnest.utils.serialization.DataSerializer

object AuthDataSerializer : DataSerializer<AuthData>(AuthData.serializer()) {
  override val defaultInstance: AuthData = AuthData(modifiedAt = 0)
}
