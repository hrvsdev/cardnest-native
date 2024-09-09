package app.cardnest.screens.user.account

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.cardnest.R
import app.cardnest.data.userState
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.security.MessageDigest
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class AccountViewModel() : ViewModel() {
  val user = userState.asStateFlow()

  fun signInWithGoogle(ctx: Context) {
    val credentialManager = CredentialManager.create(ctx)

    @OptIn(ExperimentalUuidApi::class)
    val rawNonce = Uuid.random().toByteArray()
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

    viewModelScope.launch {
      val result = try {
        credentialManager.getCredential(context = ctx, request = request)
      } catch (e: Exception) {
        Log.e("AccountViewModel", "Error getting credential", e)
        return@launch
      }

      val credential = result.credential

      val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
      val authCredential = googleIdTokenCredential.idToken

      val googleCredential = GoogleAuthProvider.getCredential(authCredential, null)

      Firebase.auth.signInWithCredential(googleCredential).addOnCompleteListener {
        if (!it.isSuccessful) {
          Log.e("AccountViewModel", "Error signing in with Google", it.exception)
        }
      }
    }
  }

  fun signOut() {
    Firebase.auth.signOut()
  }
}

