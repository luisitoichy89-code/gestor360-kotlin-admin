package org.luisito.admin360.data.repository

sealed class LoginResult {
    data class Success(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthRepository {
    
    suspend fun login(email: String, password: String): LoginResult {
        // Temporal: acepta cualquier login
        return if (email.isNotBlank() && password.isNotBlank()) {
            LoginResult.Success(email)
        } else {
            LoginResult.Error("Credenciales inválidas")
        }
    }
}
