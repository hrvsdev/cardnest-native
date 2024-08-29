package app.cardnest.di

import app.cardnest.AppViewModel
import app.cardnest.authDataStore
import app.cardnest.cardsDataStore
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardEditorViewModel
import app.cardnest.db.auth.AuthDataOperations
import app.cardnest.db.auth.AuthRepository
import app.cardnest.db.card.CardDataOperations
import app.cardnest.db.card.CardRepository
import app.cardnest.screens.add.editor.AddCardViewModel
import app.cardnest.screens.home.HomeViewModel
import app.cardnest.screens.home.card.CardViewModel
import app.cardnest.screens.home.card.editor.UpdateCardViewModel
import app.cardnest.screens.pin.create.ConfirmPinViewModel
import app.cardnest.screens.pin.create.CreatePinViewModel
import app.cardnest.screens.pin.enter.EnterPinViewModel
import app.cardnest.screens.pin.verify.VerifyPinViewModel
import app.cardnest.screens.user.security.SecurityViewModel
import app.cardnest.utils.card.defaultCard
import app.cardnest.utils.crypto.CryptoManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single { CryptoManager }

  single { AuthDataOperations(androidContext().authDataStore) }
  single { AuthRepository(get()) }
  single { AuthManager(get(), get()) }

  single { CardDataOperations(androidContext().cardsDataStore) }
  single { CardRepository(get()) }
  single { CardDataManager(get(), get()) }

  factory { defaultCard() }

  single { Actions() }

  viewModel { CardEditorViewModel(it.getOrNull() ?: get()) }

  viewModel { AppViewModel(get()) }

  viewModel { HomeViewModel(get()) }
  viewModel { CardViewModel(get(), it.get(), it.get()) }
  viewModel { UpdateCardViewModel(get(), it.get(), it.get()) }

  viewModel { AddCardViewModel(get(), it.get()) }

  viewModel { SecurityViewModel(get(), get(), get(), it.get(), it.get()) }

  viewModel { CreatePinViewModel(it.get()) }
  viewModel { ConfirmPinViewModel(get(), get(), get(), it.get()) }
  viewModel { EnterPinViewModel(get(), it.get()) }
  viewModel { VerifyPinViewModel(get(), get()) }
}
