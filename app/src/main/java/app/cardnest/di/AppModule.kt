package app.cardnest.di

import app.cardnest.AppViewModel
import app.cardnest.authDataStore
import app.cardnest.cardsDataStore
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.card.CardEditorViewModel
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.user.UserManager
import app.cardnest.db.auth.AuthRepository
import app.cardnest.db.card.CardRepository
import app.cardnest.db.preferences.PreferencesRepository
import app.cardnest.preferencesDataStore
import app.cardnest.screens.add.editor.AddCardViewModel
import app.cardnest.screens.home.HomeViewModel
import app.cardnest.screens.home.card.CardViewModel
import app.cardnest.screens.home.card.editor.UpdateCardViewModel
import app.cardnest.screens.password.change.ChangePasswordViewModel
import app.cardnest.screens.password.create.CreatePasswordViewModel
import app.cardnest.screens.password.sign_in.SignInWithPasswordViewModel
import app.cardnest.screens.password.unlock.UnlockWithPasswordViewModel
import app.cardnest.screens.password.unlock.help.UnlockWithPasswordHelpViewModel
import app.cardnest.screens.password.verify.VerifyPasswordViewModel
import app.cardnest.screens.pin.create.confirm.ConfirmPinViewModel
import app.cardnest.screens.pin.create.create.CreatePinViewModel
import app.cardnest.screens.pin.unlock.UnlockWithPinViewModel
import app.cardnest.screens.pin.unlock.help.UnlockWithPinHelpViewModel
import app.cardnest.screens.pin.verify.VerifyPinViewModel
import app.cardnest.screens.user.account.AccountViewModel
import app.cardnest.screens.user.app_info.updates.UpdatesViewModel
import app.cardnest.screens.user.manage_data.ManageDataViewModel
import app.cardnest.screens.user.security.SecurityViewModel
import app.cardnest.screens.user.user_interface.UserInterfaceViewModel
import app.cardnest.utils.card.defaultCard
import app.cardnest.utils.crypto.CryptoManager
import app.cardnest.utils.updates.UpdatesManager
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
  single { CryptoManager }

  single { UserManager(get(), get()) }

  single { AuthRepository(androidContext().authDataStore) }
  single { AuthManager(get(), get()) }

  single { CardRepository(androidContext().cardsDataStore) }
  single { CardDataManager(get(), get()) }

  single { PreferencesRepository(androidContext().preferencesDataStore) }
  single { PreferencesManager(get()) }

  single { HttpClient(Android) }
  single { UpdatesManager(get()) }

  factory { defaultCard() }

  viewModel { CardEditorViewModel(it.getOrNull() ?: get()) }

  viewModel { AppViewModel(get(), get(), get(), get()) }

  viewModel { HomeViewModel(get()) }
  viewModel { CardViewModel(get(), it.get()) }
  viewModel { UpdateCardViewModel(get(), it.get()) }

  viewModel { AddCardViewModel(get(), it.get()) }

  viewModel { AccountViewModel(get(), it.get()) }
  viewModel { SecurityViewModel(get(), get(), it.get(), it.get()) }
  viewModel { UserInterfaceViewModel(get()) }
  viewModel { ManageDataViewModel(get(), it.get()) }
  viewModel { UpdatesViewModel(get(), get()) }

  viewModel { CreatePinViewModel(it.get()) }
  viewModel { ConfirmPinViewModel(get(), it.get()) }
  viewModel { UnlockWithPinViewModel(get(), it.get()) }
  viewModel { UnlockWithPinHelpViewModel(get(), it.get()) }
  viewModel { VerifyPinViewModel(get(), it.get()) }

  viewModel { CreatePasswordViewModel(get(), it.get()) }
  viewModel { SignInWithPasswordViewModel(get(), it.get()) }
  viewModel { UnlockWithPasswordViewModel(get(), it.get()) }
  viewModel { UnlockWithPasswordHelpViewModel(get(), it.get()) }
  viewModel { ChangePasswordViewModel(get(), it.get()) }
  viewModel { VerifyPasswordViewModel(get(), it.get()) }
}
