package app.cardnest.data.auth

import app.cardnest.utils.extensions.toEncoded
import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
  val salt: ByteArray? = null,
  val encryptedDek: EncryptedData? = null,
  val encryptedBiometricsDek: EncryptedData? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)

@Serializable
data class EncryptedData(val ciphertext: ByteArray, val iv: ByteArray)

data class AuthDataForDb(
  val salt: String? = null,
  val encryptedDek: EncryptedDataForDb? = null,
  val encryptedBiometricsDek: EncryptedDataForDb? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)

data class EncryptedDataForDb(val ciphertext: String, val iv: String)
data class EncryptedDataForDbNullable(val ciphertext: String? = null, val iv: String? = null)

fun AuthData.toEncoded() = AuthDataForDb(
  salt = salt?.toEncoded(),
  encryptedDek = encryptedDek?.toEncoded(),
  encryptedBiometricsDek = encryptedBiometricsDek?.toEncoded(),
  hasCreatedPin = hasCreatedPin,
  hasBiometricsEnabled = hasBiometricsEnabled
)

fun AuthDataForDb.toDecoded() = AuthData(
  salt = salt?.toByteArray(),
  encryptedDek = encryptedDek?.toDecoded(),
  encryptedBiometricsDek = encryptedBiometricsDek?.toDecoded(),
  hasCreatedPin = hasCreatedPin,
  hasBiometricsEnabled = hasBiometricsEnabled
)

fun EncryptedData.toEncoded() = EncryptedDataForDb(ciphertext = ciphertext.toEncoded(), iv = iv.toEncoded())
fun EncryptedDataForDb.toDecoded() = EncryptedData(ciphertext = ciphertext.toByteArray(), iv = iv.toByteArray())
