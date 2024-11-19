package app.cardnest.data.auth

import app.cardnest.utils.extensions.decoded
import app.cardnest.utils.extensions.encoded
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
  val password: PasswordData? = null,
  val pin: PinData? = null,
  val biometrics: BiometricsData? = null,
)

@Serializable
data class PasswordData(
  val salt: String,
  val encryptedDek: EncryptedDataEncoded,
  val modifiedAt: Long,
)

@Serializable
data class PinData(
  val salt: String,
  val encryptedDek: EncryptedDataEncoded,
  val modifiedAt: Long,
)

@Serializable
data class BiometricsData(
  val encryptedDek: EncryptedDataEncoded,
  val modifiedAt: Long,
)

@Serializable
data class EncryptedData(val ciphertext: ByteArray, val iv: ByteArray)

@Serializable
data class EncryptedDataEncoded(val ciphertext: String, val iv: String)

data class RemoteAuthData(
  val password: PasswordData
)

data class RemoteAuthDataNullable(
  val password: PasswordDataNullable? = null
)

data class PasswordDataNullable(
  val salt: String? = null,
  val encryptedDek: EncryptedDataEncodedNullable? = null,
  val modifiedAt: Long? = null
)

data class EncryptedDataEncodedNullable(val ciphertext: String? = null, val iv: String? = null)

val EncryptedData.encoded get() = EncryptedDataEncoded(ciphertext.encoded, iv.encoded)
val EncryptedDataEncoded.decoded get() = EncryptedData(ciphertext.decoded, iv.decoded)
