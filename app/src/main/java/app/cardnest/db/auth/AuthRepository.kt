package app.cardnest.db.auth

import app.cardnest.data.auth.AuthData

class AuthRepository(private val dataOperations: AuthDataOperations) {
  fun getAuthData() = dataOperations.getAuthData()
  suspend fun setAuthData(authData: AuthData) = dataOperations.setAuthData(authData)
}
