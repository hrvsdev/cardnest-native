package app.cardnest

import android.app.Application
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.dataStore
import app.cardnest.di.appModule
import app.cardnest.state.CardsDataSerializer
import app.cardnest.ui.theme.CardNestTheme
import org.koin.android.ext.koin.androidContext
import org.koin.androix.startup.KoinStartup.onKoinStartup
import org.koin.core.annotation.KoinExperimentalAPI

val Context.cardsDataStore by dataStore("cards_data.json", CardsDataSerializer)

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    installSplashScreen()
    enableEdgeToEdge(SystemBarStyle.dark(Color.TRANSPARENT), SystemBarStyle.dark(Color.TRANSPARENT))
    setContent {
      CardNestTheme {
        App()
      }
    }
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
