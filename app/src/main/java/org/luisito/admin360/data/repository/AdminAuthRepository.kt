package org.luisito.admin360.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import org.luisito.admin360.data.SupabaseClientProvider

class AdminAuthRepository {

    suspend fun login(email: String, password: String): LoginResult {
        return try {
            val supabase = SupabaseClientProvider.client
            val response = supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val user = response.user
            if (user != null) {
                LoginResult.Success(user.id)
            } else {
                LoginResult.Error("Credenciales inválidas")
            }
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

sealed class LoginResult {
    data class Success(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}
