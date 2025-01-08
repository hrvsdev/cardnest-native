package app.cardnest.data.card

import androidx.annotation.Keep
import app.cardnest.utils.serialization.PersistentMapSerializer
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable
import java.io.Serializable as JavaSerializable

@Keep
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

@Keep
@Serializable
sealed class CardRecords {
  @Keep
  @Serializable
  data class Encrypted(
    @Serializable(with = PersistentMapSerializer::class)
    val cards: PersistentMap<String, CardEncrypted> = persistentMapOf()
  ) : CardRecords()

  @Keep
  @Serializable
  data class Unencrypted(
    @Serializable(with = PersistentMapSerializer::class)
    val cards: PersistentMap<String, CardUnencrypted> = persistentMapOf()
  ) : CardRecords()
}

@Keep
@Serializable
sealed class CardData {
  @Keep
  @Serializable
  data class Encrypted(val encrypted: CardEncrypted) : CardData()

  @Keep
  @Serializable
  data class Unencrypted(val unencrypted: CardUnencrypted) : CardData()
}

@Keep
@Serializable
data class CardEncrypted(
  val id: String,
  val data: CardEncryptedData,
  val modifiedAt: Long
)

@Keep
@Serializable
data class CardUnencrypted(
  val id: String,
  val data: Card,
  val modifiedAt: Long
) : JavaSerializable

@Keep
@Serializable
data class CardEncryptedData(
  val cipherText: String,
  val iv: String,
)

@Keep
data class CardEncryptedNullable(
  val id: String? = null,
  val data: CardEncryptedDataNullable? = null,
  val modifiedAt: Long? = null
)

@Keep
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
