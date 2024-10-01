package app.cardnest.data.card

import app.cardnest.components.core.AppTextFieldError
import app.cardnest.utils.serialization.AppPersistentMapSerializer
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class Card(
  val number: String,
  val expiry: String,
  val cardholder: String,
  val issuer: String,
  val cvv: String,
  val network: PaymentNetwork,
  val theme: CardTheme
)

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
)

@Serializable
data class CardEncryptedData(
  val cipherText: ByteArray,
  val iv: ByteArray,
)

@Serializable
data class CardEncryptedEncodedForDb(
  val id: String,
  val data: CardEncryptedDataEncodedForDb,
  val modifiedAt: Long
)

@Serializable
data class CardEncryptedEncodedForDbNullable(
  val id: String? = null,
  val data: CardEncryptedDataEncodedForDbNullable? = null,
  val modifiedAt: Long? = null
)

@Serializable
data class CardEncryptedDataEncodedForDb(
  val cipherText: String,
  val iv: String,
)

@Serializable
data class CardEncryptedDataEncodedForDbNullable(
  val cipherText: String? = null,
  val iv: String? = null
)

data class CardErrorsState(
  val number: AppTextFieldError = CardNumberError(false),
  val expiry: AppTextFieldError = CardExpiryError(false),
  val cardholder: AppTextFieldError = CardholderError(false),
  val cvv: AppTextFieldError = CardCvvError(false)
)

data class CardNumberError(override val hasError: Boolean) :
  AppTextFieldError("Card number must be exact 16 digits long")

data class CardExpiryError(override val hasError: Boolean) :
  AppTextFieldError("Expiry date must be a valid date in MM/YY format")

data class CardholderError(override val hasError: Boolean) :
  AppTextFieldError("Cardholder name must be at least 2 characters long")

data class CardCvvError(override val hasError: Boolean) :
  AppTextFieldError("CVV must be exact 3 digits long or you can leave it empty")

data class DisplayCardDetails(
  val number: String,
  val expiry: String,
  val cardholder: String,
  val issuer: String
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
