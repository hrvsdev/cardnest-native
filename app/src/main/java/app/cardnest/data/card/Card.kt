package app.cardnest.data.card

import app.cardnest.utils.serialization.AppPersistentMapSerializer
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Serializable
data class Card(
  val number: String,
  val expiry: String,
  val cardholder: String,
  val issuer: String,
  val cvv: String,
  val network: PaymentNetwork,
  val theme: CardTheme
) : JavaSerializable

@Serializable
sealed class CardRecords {
  @Serializable
  data class Encrypted(
    @Serializable(with = AppPersistentMapSerializer::class)
    val cards: PersistentMap<String, CardEncrypted> = persistentMapOf()
  ) : CardRecords()

  @Serializable
  data class Unencrypted(
    @Serializable(with = AppPersistentMapSerializer::class)
    val cards: PersistentMap<String, CardUnencrypted> = persistentMapOf()
  ) : CardRecords()
}

@Serializable
sealed class CardData {
  @Serializable
  data class Encrypted(val encrypted: CardEncrypted) : CardData()

  @Serializable
  data class Unencrypted(val unencrypted: CardUnencrypted) : CardData()
}

@Serializable
data class CardEncrypted(
  val id: String,
  val data: CardEncryptedData,
  val modifiedAt: Long
)

@Serializable
data class CardUnencrypted(
  val id: String,
  val data: Card,
  val modifiedAt: Long
) : JavaSerializable

@Serializable
data class CardEncryptedData(
  val cipherText: String,
  val iv: String,
)

data class CardEncryptedNullable(
  val id: String? = null,
  val data: CardEncryptedDataNullable? = null,
  val modifiedAt: Long? = null
)

data class CardEncryptedDataNullable(
  val cipherText: String? = null,
  val iv: String? = null
)

enum class PaymentNetwork {
  VISA,
  MASTERCARD,
  AMEX,
  DISCOVER,
  DINERS,
  RUPAY,
  OTHER
}

enum class CardFocusableField {
  NUMBER,
  EXPIRY,
  CARDHOLDER,
  ISSUER,
  NETWORK,
}

enum class CardTheme {
  RED,
  ORANGE,
  YELLOW,
  GREEN,
  EMERALD,
  TEAL,
  CYAN,
  SKY,
  BLUE,
  INDIGO,
  VIOLET,
  PURPLE,
  FUCHSIA,
  PINK,
  ROSE,
}
