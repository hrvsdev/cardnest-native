package app.cardnest.data.user

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import app.cardnest.R
import app.cardnest.data.User
import app.cardnest.data.appDataState
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.initialUserState
import app.cardnest.data.passwordData
import app.cardnest.data.remotePasswordData
import app.cardnest.data.userState
import app.cardnest.utils.extensions.checkNotNull
import app.cardnest.utils.extensions.combineCollectLatest
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class UserManager(private val authManager: AuthManager, private val cardDataManager: CardDataManager) {
  suspend fun signInWithGoogle(ctx: Context): SignInResult {
    try {
      Firebase.auth.signOut()
      Firebase.auth.signInWithCredential(getGoogleAuthCredential(ctx)).await()
    } catch (e: FirebaseAuthException) {
      throw when (e) {
        is FirebaseAuthInvalidUserException -> Exception("User is deleted or disabled", e)
        is FirebaseAuthInvalidCredentialsException -> Exception("Invalid credentials", e)
        else -> Exception("Error signing in with Google", e)
      }
    }

    val remotePasswordData = appDataState.first { it.remoteAuth }.let { remotePasswordData.value }
    val isUserNew = remotePasswordData == null

    return if (isUserNew) SignInResult.CREATE_PASSWORD else SignInResult.ENTER_PASSWORD
  }

  suspend fun continueSignInByCreatingPassword(password: String) {
    authManager.createAndSetPassword(password)
    userState.first { it != null }

    appDataState.update { it.copy(areCardsMerging = true) }
    cardDataManager.mergeCards()
  }

  suspend fun continueSignInByEnteringPassword(password: String) {
    authManager.setLocalPassword(password)
    userState.first { it != null }

    appDataState.update { it.copy(areCardsMerging = true) }
    cardDataManager.mergeCards()
  }

  suspend fun signOut() {
    deleteLocalData()
    Firebase.auth.signOut()
  }

  suspend fun deleteUser(ctx: Context) {
    try {
      Firebase.auth.currentUser?.reauthenticate(getGoogleAuthCredential(ctx))?.await()
    } catch (e: FirebaseAuthException) {
      throw when (e) {
        is FirebaseAuthInvalidUserException -> Exception("User is deleted or disabled", e)
        is FirebaseAuthInvalidCredentialsException -> Exception("Invalid credentials for selected account", e)
        else -> Exception("Error re-authenticating user", e)
      }
    }

    val currentUser = Firebase.auth.currentUser.checkNotNull { "Sign-in first to delete user" }

    try {
      deleteRemoteData()
      deleteLocalData()
      currentUser.delete().await()
    } catch (e: FirebaseAuthException) {
      throw Exception("Error deleting user", e)
    }
  }

  suspend fun collectUser() {
    val userFlow = callbackFlow {
      val listener = Firebase.auth.addAuthStateListener { trySend(it.currentUser?.toUser()) }
      awaitClose { Firebase.auth.removeAuthStateListener { listener } }
    }

    combineCollectLatest(userFlow, passwordData) { user, passwordData ->
      initialUserState.update { user }
      userState.update { if (passwordData == null) null else user }
      appDataState.update { it.copy(user = true) }
    }
  }

  private suspend fun getGoogleAuthCredential(ctx: Context): AuthCredential {
    val credentialManager = CredentialManager.create(ctx)

    val rawNonce = UUID.randomUUID().toString().toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(rawNonce)
    val nonce = digest.joinToString("") { "%02x".format(it) }

    val googleIdOption = GetGoogleIdOption.Builder()
      .setFilterByAuthorizedAccounts(false)
      .setServerClientId(ctx.getString(R.string.google_auth_client_id))
      .setAutoSelectEnabled(false)
      .setNonce(nonce)
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
    val googleAuthCredential = GoogleAuthProvider.getCredential(googleIdTokenCredential.idToken, null)

    return googleAuthCredential
  }

  private suspend fun deleteLocalData() {
    cardDataManager.resetLocalCards()
    authManager.resetLocalAuthData()
  }

  private suspend fun deleteRemoteData() {
    cardDataManager.resetRemoteCards()
    authManager.resetRemoteAuthData()
  }

  private fun FirebaseUser.toUser(): User {
    val fullName = displayName ?: "Anonymous"
    return User(uid = uid, name = fullName.split(" ").first(), fullName = fullName)
  }
}
