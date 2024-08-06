package app.cardnest.data.serializables

import app.cardnest.data.CardFullProfile
import app.cardnest.utils.serialization.AppPersistentMapSerializer
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class CardRecords(
  @Serializable(with = AppPersistentMapSerializer::class)
  val cards: PersistentMap<String, CardDataWithId> = persistentMapOf()
)

@Serializable
data class CardDataWithId(
  val id: String,
  val data: CardData
)

@Serializable
sealed class CardData {
  @Serializable
  data class Encrypted(val card: CardEncrypted) : CardData()

  @Serializable
  data class Unencrypted(val card: CardFullProfile) : CardData()
}

@Serializable
data class CardEncrypted(
  val cipherText: ByteArray,
  val iv: ByteArray,
  val salt: ByteArray
)

@Serializable
data class CardRecord(
  val id: String,
  val plainData: CardFullProfile
)
