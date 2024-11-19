package app.cardnest.data.user

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import app.cardnest.R
import app.cardnest.data.User
import app.cardnest.data.auth.AuthManager
import app.cardnest.data.authDataLoadState
import app.cardnest.data.card.CardDataManager
import app.cardnest.data.initialUserState
import app.cardnest.data.passwordData
import app.cardnest.data.remotePasswordData
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import java.util.UUID

class UserManager(private val authManager: AuthManager, private val cardDataManager: CardDataManager) {
  suspend fun signInWithGoogle(ctx: Context): SignInResult {
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

    val remotePasswordData = authDataLoadState.first { it.hasRemoteLoaded }.let { remotePasswordData.value }
    val isUserNew = remotePasswordData == null

    return if (isUserNew) SignInResult.CREATE_PASSWORD else SignInResult.ENTER_PASSWORD
  }

  suspend fun continueSignInByCreatingPassword(password: String) {
    authManager.createAndSetPassword(password)
    userState.update { initialUserState.value }
  }

  suspend fun continueSignInByEnteringPassword(password: String) {
    authManager.setLocalPassword(password)
    userState.update { initialUserState.value }
  }

  suspend fun initialSignOut() {
    authManager.removeLocalPassword()
    Firebase.auth.signOut()
  }

  suspend fun signOut() {
    initialSignOut()
    authManager.resetAuthData()
    cardDataManager.resetCards()
  }

  suspend fun collectUser() {
    val userFlow = callbackFlow {
      val listener = Firebase.auth.addAuthStateListener { trySend(it.currentUser?.toUser()) }
      awaitClose { Firebase.auth.removeAuthStateListener { listener } }
    }

    val userIfSignedInWithPasswordFlow = userFlow.combine(passwordData) { user, passwordData ->
      when {
        user == null -> Users(null, null)
        passwordData == null -> Users(user, null)
        else -> Users(user, user)
      }
    }

    userIfSignedInWithPasswordFlow.collectLatest { users ->
      initialUserState.update { users.initial }
      userState.update { users.current }
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

private data class Users(val initial: User?, val current: User?)
