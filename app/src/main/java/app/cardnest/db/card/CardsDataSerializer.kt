package app.cardnest.db.card

import app.cardnest.data.card.CardRecords
import app.cardnest.utils.serialization.DataSerializer

object CardsDataSerializer : DataSerializer<CardRecords>(CardRecords.serializer()) {
  override val defaultInstance: CardRecords = CardRecords()
}
