package app.cardnest.db.auth

import app.cardnest.data.auth.AuthRecord
import app.cardnest.utils.serialization.DataSerializer

object AuthDataSerializer : DataSerializer<AuthRecord>(AuthRecord.serializer()) {
  override val defaultInstance = AuthRecord()
}
