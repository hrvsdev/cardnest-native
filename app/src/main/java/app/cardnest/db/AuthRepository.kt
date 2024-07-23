package app.cardnest.db

import app.cardnest.data.serializables.AuthData

class AuthRepository(private val dataOperations: AuthDataOperations) {
  fun getAuthData() = dataOperations.getAuthData()
  suspend fun setAuthData(authData: AuthData) = dataOperations.setAuthData(authData)
}
