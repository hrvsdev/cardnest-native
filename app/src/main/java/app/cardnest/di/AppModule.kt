package app.cardnest.di

import app.cardnest.cardsDataStore
import app.cardnest.db.CardDataOperations
import app.cardnest.db.CardRepository
import app.cardnest.state.card.CardEditorViewModel
import app.cardnest.state.card.CardsDataViewModel
import app.cardnest.state.card.defaultCard
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single { CardDataOperations(androidContext().cardsDataStore) }
  single { CardRepository(get()) }

  factory { defaultCard() }

  viewModel { CardEditorViewModel(it.getOrNull() ?: get()) }
  viewModel { CardsDataViewModel(get()) }
}
