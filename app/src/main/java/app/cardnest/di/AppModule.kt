package app.cardnest.di

import app.cardnest.authDataStore
import app.cardnest.cardsDataStore
import app.cardnest.db.AuthDataOperations
import app.cardnest.db.AuthRepository
import app.cardnest.db.CardDataOperations
import app.cardnest.db.CardRepository
import app.cardnest.state.actions.ActionsViewModel
import app.cardnest.state.auth.AuthDataViewModel
import app.cardnest.state.card.CardEditorViewModel
import app.cardnest.state.card.CardsDataViewModel
import app.cardnest.state.card.defaultCard
import app.cardnest.utils.crypto.CryptoManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single { CryptoManager }

  single { CardDataOperations(androidContext().cardsDataStore) }
  single { CardRepository(get()) }

  single { AuthDataOperations(androidContext().authDataStore) }
  single { AuthRepository(get()) }

  factory { defaultCard() }

  viewModel { CardEditorViewModel(it.getOrNull() ?: get()) }
  viewModel { CardsDataViewModel(get()) }

  viewModel { AuthDataViewModel(get(), get()) }
  single { ActionsViewModel() }
}
