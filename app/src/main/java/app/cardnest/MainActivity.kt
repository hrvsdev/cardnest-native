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
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import app.cardnest.data.serializables.CardRecords
import app.cardnest.state.CardsDataSerializer
import app.cardnest.ui.theme.CardNestTheme

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

class CardNestApp : Application() {
  companion object {
    lateinit var dataStores: AppDataStoresModule
  }

  override fun onCreate() {
    super.onCreate()
    dataStores = AppDataStoresModule(this.cardsDataStore)
  }
}

class AppDataStoresModule(private val dataStore: DataStore<CardRecords>) {
  val cardsDataStore by lazy { dataStore }
}
