package com.hrvs.cardnest.state.card

import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hrvs.cardnest.data.serializables.CardRecord
import com.hrvs.cardnest.data.serializables.CardRecords
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


@OptIn(ExperimentalCoroutinesApi::class)
class CardsDataViewModel(private val ds: DataStore<CardRecords>) : ViewModel() {
  private val _state = ds.data.mapLatest { it }

  val stateAsMap = _state.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = CardRecords(),
  )

  val state = _state.mapLatest { it.cards.values.toList() }.stateIn(
    scope = viewModelScope,
    started = SharingStarted.WhileSubscribed(5000),
    initialValue = emptyList(),
  )

  fun addCard(card: CardRecord) {
    viewModelScope.launch {
      ds.updateData { it.copy(cards = it.cards.put(card.id, card)) }
    }
  }

  fun updateCard(card: CardRecord) {
    viewModelScope.launch {
      ds.updateData { it.copy(cards = it.cards.put(card.id, card)) }
    }
  }

  fun deleteCard(id: String) {
    viewModelScope.launch {
      ds.updateData { it.copy(cards = it.cards.remove(id)) }
    }
  }
}
