package app.cardnest.data.user

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import app.cardnest.R
import app.cardnest.data.User
import app.cardnest.data.auth.AuthData
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authData
import app.cardnest.data.authDataLoadState
import app.cardnest.data.authState
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.preferences.PreferencesManager
import app.cardnest.data.remoteAuthData
import app.cardnest.data.userState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID
import javax.crypto.SecretKey

class UserManager(
  private val authManager: AuthManager,
  private val cardDataManager: CardDataManager,
  private val prefsManager: PreferencesManager
) {
  suspend fun signInWithGoogle(ctx: Context) {
    val credentialManager = CredentialManager.create(ctx)

    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(false)
      .setServerClientId(ctx.getString(R.string.google_auth_client_id))
      .setAutoSelectEnabled(false)
      .setNonce(generateNonce())
      .build()

    val request = GetCredentialRequest.Builder()
      .addCredentialOption(googleIdOption)
      .build()

    val result = try {
      credentialManager.getCredential(context = ctx, request = request)
    } catch (e: GetCredentialException) {
      if (e is GetCredentialCancellationException) throw Exception()
      throw Exception("Error getting Google ID credential", e)
    }

    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
    val authCredential = googleIdTokenCredential.idToken

    val googleCredential = GoogleAuthProvider.getCredential(authCredential, null)

    try {
      Firebase.auth.signInWithCredential(googleCredential).await()
    } catch (e: FirebaseAuthException) {
      when (e) {
        is FirebaseAuthInvalidUserException -> throw Exception("User is deleted or disabled", e)
        is FirebaseAuthInvalidCredentialsException -> throw Exception("Invalid credentials", e)
        else -> throw Exception("Error signing in with Google", e)
      }
    }
  }

  suspend fun signOut() {
    prefsManager.setSync(false)
    Firebase.auth.signOut()
  }

  suspend fun collectUser() {
    val userFlow = callbackFlow {
      val listener = Firebase.auth.addAuthStateListener { trySend(it.currentUser?.toUser()) }
      awaitClose { Firebase.auth.removeAuthStateListener { listener } }
    }

    userFlow.collectLatest { d -> userState.update { d } }
  }

  suspend fun setupSync(): SyncResult {
    val remoteAuthData = authDataLoadState.first { it.hasRemoteLoaded }.let { remoteAuthData.value }
    val isUserNew = remoteAuthData == null

    val dek = authState.value.dek
    val pin = authState.value.pin

    if (isUserNew) {
      return if (dek == null) {
        SyncResult.CREATE_PIN
      } else {
        syncDataAndUpdateState(dek)
      }
    }

    if (pin == null) return getPreviousOrNewPinRequired(remoteAuthData)

    val remoteDek = authManager.getRemoteDek(pin)
    val isLocalPinSameAsRemote = remoteDek != null

    return if (isLocalPinSameAsRemote) {
      syncDataAndUpdateState(remoteDek)
    } else {
      getPreviousOrNewPinRequired(remoteAuthData)
    }
  }

  suspend fun continueSetupSyncWithDifferentPin(pin: String): SyncResult {
    val remoteDek = authManager.getRemoteDek(pin) ?: return SyncResult.ERROR

    authManager.syncAuthState(pin, remoteDek)
    return syncDataAndUpdateState(remoteDek)
  }

  private suspend fun syncDataAndUpdateState(dek: SecretKey): SyncResult {
    authManager.syncAuthData()
    cardDataManager.syncCards(dek)

    prefsManager.setSync(true)

    return SyncResult.SUCCESS
  }

  private fun getPreviousOrNewPinRequired(remoteAuthData: AuthData): SyncResult {
    val authData = authData.value
    return if (authData != null && authData.modifiedAt < remoteAuthData.modifiedAt) {
      SyncResult.NEW_PIN_REQUIRED
    } else {
      SyncResult.PREVIOUS_PIN_REQUIRED
    }
  }

  private fun generateNonce(): String {
    val rawNonce = UUID.randomUUID().toString().toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(rawNonce)
    return digest.joinToString("") { "%02x".format(it) }
  }

  private fun FirebaseUser.toUser(): User {
    val fullName = displayName ?: "Anonymous"
    return User(uid = uid, name = fullName.split(" ").first(), fullName = fullName)
  }
}
