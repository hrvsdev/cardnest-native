package app.cardnest

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.dataStore
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cardnest.db.auth.AuthDataSerializer
import app.cardnest.db.card.CardsDataSerializer
import app.cardnest.db.preferences.PreferencesDataSerializer
import app.cardnest.di.appModule
import app.cardnest.ui.theme.CardNestTheme
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.compose.koinViewModel
import org.koin.androix.startup.KoinStartup.onKoinStartup
import org.koin.core.annotation.KoinExperimentalAPI

val Context.cardsDataStore by dataStore("cards_data.json", CardsDataSerializer)
val Context.authDataStore by dataStore("auth_data.json", AuthDataSerializer)
val Context.preferencesDataStore by dataStore("preferences_data.json", PreferencesDataSerializer)

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(Color.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(Color.TRANSPARENT)
    )

    setContent { AppRoot(installSplashScreen()) }

    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
  }
}

@OptIn(KoinExperimentalAPI::class)
class CardNestApp : Application() {
  init {
    onKoinStartup {
      androidContext(this@CardNestApp)
      modules(appModule)
    }
  }

  override fun onCreate() {
    super.onCreate()
  }
}
