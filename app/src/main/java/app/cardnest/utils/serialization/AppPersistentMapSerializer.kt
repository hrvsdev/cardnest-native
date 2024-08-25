package app.cardnest.utils.serialization

import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.toPersistentMap
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

class AppPersistentMapSerializer<T>(
  private val keySerializer: KSerializer<String>,
  private val valueSerializer: KSerializer<T>
) : KSerializer<PersistentMap<String, T>> {

  override val descriptor: SerialDescriptor = valueSerializer.descriptor

  override fun serialize(encoder: Encoder, value: PersistentMap<String, T>) {
    return MapSerializer(keySerializer, valueSerializer).serialize(encoder, value)
  }

  override fun deserialize(decoder: Decoder): PersistentMap<String, T> {
    return MapSerializer(keySerializer, valueSerializer).deserialize(decoder).toPersistentMap()
  }
}
