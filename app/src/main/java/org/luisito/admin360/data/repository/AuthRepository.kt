package org.luisito.admin360.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import org.luisito.admin360.data.remote.SupabaseProvider

sealed class LoginResult {
    data class Success(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthRepository {
    
    suspend fun login(email: String, password: String): LoginResult {
        return try {
            val supabase = SupabaseProvider.client
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            LoginResult.Success(email)
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "Error de conexión")
        }
    }
    
    suspend fun sendPasswordRecovery(email: String): Boolean {
        return try {
            val supabase = SupabaseProvider.client
            supabase.auth.resetPasswordForEmail(email)
            true
        } catch (e: Exception) {
            false
        }
    }
}
