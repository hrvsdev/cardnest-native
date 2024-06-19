package com.hrvs.cardnest.state.card

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.hrvs.cardnest.cardsDataStore
import com.hrvs.cardnest.data.serializables.CardRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Composable
fun getCardsMapFlow(ctx: Context): Flow<Map<String, CardRecord>> {
  return ctx.cardsDataStore.data.map { it.cards }
}

@Composable
fun getCards(ctx: Context): List<CardRecord> {
  return getCardsMapFlow(ctx).collectAsState(emptyMap()).value.values.toList()
}

@Composable
fun getCard(ctx: Context, id: String): CardRecord? {
  return getCardsMapFlow(ctx).map { it[id] }.collectAsState(null).value
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

