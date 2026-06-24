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
        
        return try {
            val response = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val userId = response.user?.id ?: return LoginResult.Error("No se pudo obtener el usuario")
            LoginResult.Success(userId)
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "Error de conexión")
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
