package app.cardnest.data.auth

import app.cardnest.utils.extensions.decoded
import app.cardnest.utils.extensions.encoded
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
  val salt: String? = null,
  val encryptedDek: EncryptedDataEncoded? = null,
  val encryptedBiometricsDek: EncryptedDataEncoded? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)

@Serializable
data class EncryptedData(val ciphertext: ByteArray, val iv: ByteArray)

@Serializable
data class EncryptedDataEncoded(val ciphertext: String, val iv: String)

data class AuthDataRemote(
  val salt: String,
  val encryptedDek: EncryptedDataEncoded,
  val hasCreatedPin: Boolean = true,
)

data class AuthDataRemoteNullable(
  val salt: String? = null,
  val encryptedDek: EncryptedDataEncodedNullable? = null,
  val hasCreatedPin: Boolean = true
)

data class EncryptedDataEncodedNullable(val ciphertext: String? = null, val iv: String? = null)

val EncryptedData.encoded get() = EncryptedDataEncoded(ciphertext.encoded, iv.encoded)
val EncryptedDataEncoded.decoded get() = EncryptedData(ciphertext.decoded, iv.decoded)
