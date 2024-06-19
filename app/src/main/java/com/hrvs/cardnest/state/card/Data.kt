package com.hrvs.cardnest.state.card

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.hrvs.cardnest.cardsDataStore
import com.hrvs.cardnest.data.serializables.CardRecord
import com.hrvs.cardnest.data.serializables.CardRecords
import kotlinx.collections.immutable.PersistentMap

@Composable
fun getCardsAsMap(ctx: Context): PersistentMap<String, CardRecord> {
  return ctx.cardsDataStore.data.collectAsState(CardRecords()).value.cards
}

@Composable
fun getCards(ctx: Context): List<CardRecord> {
  return getCardsAsMap(ctx).values.toList()
}

@Composable
fun getCard(ctx: Context, id: String): CardRecord? {
  return getCardsAsMap(ctx)[id]
}

suspend fun addCard(ctx: Context, card: CardRecord) {
  ctx.cardsDataStore.updateData { it.copy(cards = it.cards.put(card.id, card)) }
}

suspend fun updateCard(ctx: Context, card: CardRecord) {
  ctx.cardsDataStore.updateData { it.copy(cards = it.cards.put(card.id, card)) }
}

suspend fun deleteCard(ctx: Context, id: String) {
  ctx.cardsDataStore.updateData { it.copy(cards = it.cards.remove(id)) }
}

