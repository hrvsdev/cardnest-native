package app.cardnest.di

import app.cardnest.AppViewModel
import app.cardnest.authDataStore
import app.cardnest.cardsDataStore
import app.cardnest.data.card.CardDataManager
import app.cardnest.db.AuthDataOperations
import app.cardnest.db.AuthRepository
import app.cardnest.db.CardDataOperations
import app.cardnest.db.CardRepository
import app.cardnest.screens.add.editor.AddCardViewModel
import app.cardnest.screens.home.HomeViewModel
import app.cardnest.screens.home.card.CardViewModel
import app.cardnest.screens.home.card.editor.UpdateCardViewModel
import app.cardnest.screens.pin.create.ConfirmPinViewModel
import app.cardnest.screens.pin.create.CreatePinViewModel
import app.cardnest.screens.pin.enter.EnterPinViewModel
import app.cardnest.screens.pin.verify.VerifyPinViewModel
import app.cardnest.screens.user.security.SecurityViewModel
import app.cardnest.state.actions.ActionsViewModel
import app.cardnest.state.auth.BiometricManager
import app.cardnest.state.card.CardEditorViewModel
import app.cardnest.state.card.defaultCard
import app.cardnest.utils.crypto.CryptoManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single { CryptoManager }
  single { BiometricManager() }

  single { CardDataOperations(androidContext().cardsDataStore) }
  single { CardRepository(get()) }
  single { CardDataManager(get(), get()) }

  single { AuthDataOperations(androidContext().authDataStore) }
  single { AuthRepository(get()) }

  factory { defaultCard() }

  single { ActionsViewModel() }

  viewModel { CardEditorViewModel(it.getOrNull() ?: get()) }

  viewModel { AppViewModel(get()) }

  viewModel { HomeViewModel(get()) }
  viewModel { CardViewModel(get(), it.get(), it.get()) }
  viewModel { UpdateCardViewModel(get(), it.get(), it.get()) }

  viewModel { AddCardViewModel(get(), it.get()) }

  viewModel { SecurityViewModel(get(), get(), get(), get(), get(), it.get(), it.get()) }

  viewModel { CreatePinViewModel(it.get()) }
  viewModel { ConfirmPinViewModel(get(), get(), get(), get(), it.get()) }
  viewModel { EnterPinViewModel(get(), get(), get(), it.get()) }
  viewModel { VerifyPinViewModel(get()) }
}
