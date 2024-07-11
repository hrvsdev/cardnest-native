package app.cardnest.data.serializables

import app.cardnest.data.CardFullProfile
import app.cardnest.utils.serialization.AppPersistentMapSerializer
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class CardRecords(
  @Serializable(with = AppPersistentMapSerializer::class)
  val cards: PersistentMap<String, CardRecord> = persistentMapOf()
)

@Serializable
data class CardRecord(
  val id: String,
  val plainData: CardFullProfile
)
