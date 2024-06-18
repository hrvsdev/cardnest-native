package com.hrvs.cardnest.data.serializables

import com.hrvs.cardnest.data.CardFullProfile
import com.hrvs.cardnest.utils.serialization.AppPersistentMapSerializer
import kotlinx.collections.immutable.PersistentMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.serialization.Serializable

@Serializable
data class CardRecords(
  @Serializable(with = AppPersistentMapSerializer::class)
  val cards: PersistentMap<String, CardRecord> = persistentMapOf()
)

@Serializable
data class CardRecord(
  val id: String,
  val plainData: CardFullProfile
)
