package app.cardnest.firebase.auth

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import app.cardnest.R
import app.cardnest.data.User
import app.cardnest.data.userState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.update
import java.security.MessageDigest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class FirebaseUserManager {
  val auth = Firebase.auth

  init {
    auth.addAuthStateListener {
      val user = it.currentUser
      userState.update { if (user != null) User(user.uid) else null }
    }
  }

  suspend fun signInWithGoogle(ctx: Context, onComplete: () -> Unit) {
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
      onComplete()

      Log.e("AccountViewModel", "Error getting credential", e)
      return
    }

    val credential = result.credential

    val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
    val authCredential = googleIdTokenCredential.idToken

    val googleCredential = GoogleAuthProvider.getCredential(authCredential, null)

    auth.signInWithCredential(googleCredential).addOnCompleteListener {
      onComplete()

      if (!it.isSuccessful) {
        Log.e("AccountViewModel", "Error signing in with Google", it.exception)
      }
    }
  }

  fun signOut() {
    auth.signOut()
  }

  @OptIn(ExperimentalUuidApi::class)
  private fun generateNonce(): String {
    val rawNonce = Uuid.random().toByteArray()
    val digest = MessageDigest.getInstance("SHA-256").digest(rawNonce)
    return digest.joinToString("") { "%02x".format(it) }
  }
}

