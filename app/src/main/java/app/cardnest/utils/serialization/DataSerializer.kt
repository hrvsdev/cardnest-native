package app.cardnest.utils.serialization

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

abstract class DataSerializer<T>(private val serializer: KSerializer<T>) : Serializer<T> {
  abstract val defaultInstance: T

  override val defaultValue: T
    get() = defaultInstance

  override suspend fun readFrom(input: InputStream): T {
    return try {
      Json.Default.decodeFromString(serializer, input.readBytes().decodeToString())
    } catch (e: SerializationException) {
      throw CorruptionException("Something went wrong while deserializing", e)
    }
  }

  override suspend fun writeTo(t: T, output: OutputStream) {
    output.write(Json.Default.encodeToString(serializer, t).toByteArray())
  }
}
