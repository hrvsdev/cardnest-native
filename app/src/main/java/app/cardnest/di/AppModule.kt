package app.cardnest.di

import app.cardnest.AppViewModel
import app.cardnest.authDataStore
import app.cardnest.cardsDataStore
import app.cardnest.data.actions.Actions
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardEditorViewModel
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.user.UserManager
import app.cardnest.db.auth.AuthRepository
import app.cardnest.db.card.CardRepository
import app.cardnest.db.preferences.PreferencesDataOperations
import app.cardnest.db.preferences.PreferencesRepository
import app.cardnest.firebase.auth.FirebaseUserManager
import app.cardnest.firebase.realtime_db.AuthDbManager
import app.cardnest.firebase.realtime_db.CardDbManager
import app.cardnest.firebase.realtime_db.ConnectionManager
import app.cardnest.preferencesDataStore
import app.cardnest.screens.add.editor.AddCardViewModel
import app.cardnest.screens.home.HomeViewModel
import app.cardnest.screens.home.card.CardViewModel
import app.cardnest.screens.home.card.editor.UpdateCardViewModel
import app.cardnest.screens.pin.create.confirm.ConfirmPinViewModel
import app.cardnest.screens.pin.create.create.CreatePinViewModel
import app.cardnest.screens.pin.enter.EnterPinViewModel
import app.cardnest.screens.pin.verify.VerifyPinViewModel
import app.cardnest.screens.pin.verify_new_pin.VerifyNewPinViewModel
import app.cardnest.screens.pin.verify_previous_pin.VerifyPreviousPinViewModel
import app.cardnest.screens.user.UserViewModel
import app.cardnest.screens.user.account.AccountViewModel
import app.cardnest.screens.user.security.SecurityViewModel
import app.cardnest.screens.user.user_interface.UserInterfaceViewModel
import app.cardnest.utils.card.defaultCard
import app.cardnest.utils.crypto.CryptoManager
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single { CryptoManager }

  single { UserManager(get(), get(), get()) }

  single { FirebaseUserManager() }
  single { CardDbManager() }
  single { AuthDbManager() }

  single { ConnectionManager() }

  single { AuthRepository(androidContext().authDataStore, get()) }
  single { AuthManager(get(), get()) }

  single { CardRepository(androidContext().cardsDataStore, get()) }
  single { CardDataManager(get(), get()) }

  single { PreferencesDataOperations(androidContext().preferencesDataStore) }
  single { PreferencesRepository(get()) }
  single { PreferencesManager(get()) }

  factory { defaultCard() }

  single { Actions() }

  viewModel { CardEditorViewModel(it.getOrNull() ?: get()) }

  viewModel { AppViewModel(get(), get(), get(), get()) }

  viewModel { HomeViewModel(get(), get(), get(), it.get(), it.get()) }
  viewModel { CardViewModel(get(), it.get(), it.get()) }
  viewModel { UpdateCardViewModel(get(), it.get(), it.get()) }

  viewModel { AddCardViewModel(get(), it.get()) }

  viewModel { UserViewModel(get(), get(), it.get(), it.get()) }
  viewModel { AccountViewModel(get(), get(), get(), get(), it.get(), it.get()) }
  viewModel { SecurityViewModel(get(), get(), get(), it.get(), it.get()) }

  viewModel { UserInterfaceViewModel(get()) }

  viewModel { CreatePinViewModel(it.get()) }
  viewModel { ConfirmPinViewModel(get(), get(), get(), it.get()) }
  viewModel { EnterPinViewModel(get(), it.get()) }
  viewModel { VerifyPinViewModel(get(), get()) }
  viewModel { VerifyPreviousPinViewModel(get(), get()) }
  viewModel { VerifyNewPinViewModel(get(), get()) }
}
