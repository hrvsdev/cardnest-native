package app.cardnest.firebase.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import app.cardnest.R
import app.cardnest.data.AppException
import app.cardnest.data.User
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FirebaseUserManager {
  private val auth = Firebase.auth

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
      if (e is GetCredentialCancellationException) throw AppException()
      throw AppException("Error getting Google ID credential", e)
    }

    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
    val authCredential = googleIdTokenCredential.idToken

    val googleCredential = GoogleAuthProvider.getCredential(authCredential, null)

    try {
      auth.signInWithCredential(googleCredential).await()
    } catch (e: FirebaseAuthException) {
      when (e) {
        is FirebaseAuthInvalidUserException -> throw AppException("User is deleted or disabled", e)
        is FirebaseAuthInvalidCredentialsException -> throw AppException("Invalid credentials", e)
        else -> throw AppException("Error signing in with Google", e)
      }
    }
  }

  fun signOut() {
    auth.signOut()
  }

  fun getUser(): Flow<User?> = callbackFlow {
    val listener = auth.addAuthStateListener {
      trySend(auth.currentUser?.toUser())
    }

    awaitClose { auth.removeAuthStateListener { listener } }
  }

  @OptIn(ExperimentalUuidApi::class)
  private fun generateNonce(): String {
    val rawNonce = Uuid.random().toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(rawNonce)
    return digest.joinToString("") { "%02x".format(it) }
  }

  fun FirebaseUser.toUser(): User {
    val fullName = displayName ?: "Anonymous"
    return User(uid = uid, name = fullName.split(" ").first(), fullName = fullName)
  }
}

