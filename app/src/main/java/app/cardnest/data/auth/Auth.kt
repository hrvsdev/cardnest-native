package app.cardnest.data.auth

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
