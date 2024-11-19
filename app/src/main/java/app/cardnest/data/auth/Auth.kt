package app.cardnest.data.auth

import app.cardnest.utils.extensions.decoded
import app.cardnest.utils.extensions.encoded
import kotlinx.serialization.Serializable

@Serializable
data class AuthRecord(
  val data: AuthData? = null,
  val initialLocalData: InitialLocalAuthData? = null,
  val pin: PinData? = null,
  val biometrics: BiometricsData? = null,
)

@Serializable
data class AuthData(
  val salt: String,
  val encryptedDek: EncryptedDataEncoded,
  val modifiedAt: Long,
)

@Serializable
data class InitialLocalAuthData(
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

data class AuthDataRemote(
  val salt: String,
  val encryptedDek: EncryptedDataEncoded,
  val modifiedAt: Long
)

data class AuthDataRemoteNullable(
  val salt: String? = null,
  val encryptedDek: EncryptedDataEncodedNullable? = null,
  val modifiedAt: Long? = null
)

data class EncryptedDataEncodedNullable(val ciphertext: String? = null, val iv: String? = null)

val EncryptedData.encoded get() = EncryptedDataEncoded(ciphertext.encoded, iv.encoded)
val EncryptedDataEncoded.decoded get() = EncryptedData(ciphertext.decoded, iv.decoded)
