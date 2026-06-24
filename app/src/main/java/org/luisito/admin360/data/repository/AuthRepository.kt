package org.luisito.admin360.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import org.luisito.admin360.data.SupabaseClientProvider

sealed class LoginResult {
    data class Success(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthRepository {

    suspend fun login(email: String, password: String): LoginResult {
        val supabase = SupabaseClientProvider.client
        
        return runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            // Si llegamos aquí, la autenticación fue exitosa
            LoginResult.Success(email)
        }.getOrElse { exception ->
            LoginResult.Error(exception.message ?: "Error de conexión")
        }
    }

    suspend fun sendPasswordRecovery(email: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.auth.resetPasswordForEmail(email)
            true
        } catch (e: Exception) {
            false
        }
    }
}
