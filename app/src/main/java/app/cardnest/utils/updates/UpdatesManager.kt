package app.cardnest.utils.updates

import app.cardnest.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class UpdatesManager(private val httpClient: HttpClient) {
  suspend fun checkForUpdates(): UpdatesResult {
    val release = try {
      val response = httpClient.get("https://api.github.com/repos/hrvsdev/cardnest-native/releases/latest")
      val body = response.bodyAsText()

      val decoder = Json { ignoreUnknownKeys = true }
      decoder.decodeFromString<Release>(body)
    } catch (e: Exception) {
      throw Exception("Failed to check for updates", e)
    }

    val currentVersionNumber = BuildConfig.VERSION_NAME
    val latestVersionNumber = release.tagName.removePrefix("v")

    return if (currentVersionNumber == latestVersionNumber || release.assets.isEmpty()) {
      UpdatesResult.NoUpdate
    } else {
      UpdatesResult.UpdateAvailable(release.tagName, release.assets.first().downloadUrl)
    }
  }
}

@Serializable
private data class Release(@SerialName("tag_name") val tagName: String, val assets: List<Asset>)

@Serializable
private data class Asset(val name: String, @SerialName("browser_download_url") val downloadUrl: String)
