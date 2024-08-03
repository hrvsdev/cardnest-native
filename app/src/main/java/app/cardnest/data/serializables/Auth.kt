package app.cardnest.data.serializables

import kotlinx.serialization.Serializable

@Serializable
data class AuthData(
  val salt: ByteArray? = null,
  val encryptedRandomKey: EncryptedData? = null,
  val hasCreatedPin: Boolean = false,
  val hasBiometricsEnabled: Boolean = false
)

@Serializable
data class EncryptedData(val ciphertext: ByteArray, val iv: ByteArray)
