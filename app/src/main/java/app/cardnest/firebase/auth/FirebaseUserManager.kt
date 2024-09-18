package app.cardnest.firebase.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import app.cardnest.R
import app.cardnest.data.User
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.security.MessageDigest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FirebaseUserManager {
  private val auth = Firebase.auth

  suspend fun signInWithGoogle(ctx: Context, onError: () -> Unit, onSuccess: (User) -> Unit) {
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
    } catch (e: Exception) {
      onError()

      Log.e("AccountViewModel", "Error getting credential", e)
      return
    }

    val credential = result.credential

    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
    val authCredential = googleIdTokenCredential.idToken

    val googleCredential = GoogleAuthProvider.getCredential(authCredential, null)

    auth.signInWithCredential(googleCredential).addOnCompleteListener {
      if (it.isSuccessful) {
        val user = it.result.user
        if (user != null) onSuccess(user.toUser()) else onError()
      } else {
        onError()
        Log.e("AccountViewModel", "Error signing in with Google", it.exception)
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

