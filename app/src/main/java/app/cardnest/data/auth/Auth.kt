package app.cardnest.data.auth

import app.cardnest.utils.extensions.toDecoded
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

data class AuthDataForDbNullable(
  val salt: String? = null,
  val encryptedDek: EncryptedDataForDbNullable? = null,
  val encryptedBiometricsDek: EncryptedDataForDbNullable? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)

data class EncryptedDataForDbNullable(val ciphertext: String? = null, val iv: String? = null)

fun AuthData.toEncoded() = AuthDataForDbNullable(
  salt = salt?.toEncoded(),

  encryptedDek = EncryptedDataForDbNullable(
    ciphertext = encryptedDek?.ciphertext?.toEncoded(),
    iv = encryptedDek?.iv?.toEncoded()
  ),

  encryptedBiometricsDek = EncryptedDataForDbNullable(
    ciphertext = encryptedBiometricsDek?.ciphertext?.toEncoded(),
    iv = encryptedBiometricsDek?.iv?.toEncoded()
  ),

  hasCreatedPin = hasCreatedPin,
  hasBiometricsEnabled = hasBiometricsEnabled
)

fun AuthDataForDbNullable.toDecoded() = AuthData(
  salt = salt?.toDecoded(),

  encryptedDek = if (encryptedDek?.ciphertext != null && encryptedDek.iv != null) EncryptedData(
    ciphertext = encryptedDek.ciphertext.toDecoded(),
    iv = encryptedDek.iv.toDecoded()
  ) else null,

  encryptedBiometricsDek = if (encryptedBiometricsDek?.ciphertext != null && encryptedBiometricsDek.iv != null) EncryptedData(
    ciphertext = encryptedBiometricsDek.ciphertext.toDecoded(),
    iv = encryptedBiometricsDek.iv.toDecoded()
  ) else null,

  hasCreatedPin = hasCreatedPin,
  hasBiometricsEnabled = hasBiometricsEnabled
)
