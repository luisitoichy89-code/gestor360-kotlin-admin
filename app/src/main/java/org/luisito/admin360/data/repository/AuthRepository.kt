package org.luisito.admin360.data.repository

import android.util.Log
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
            Log.d("AUTH", "Iniciando login...")
            val supabase = SupabaseProvider.client
            Log.d("AUTH", "Cliente Supabase creado")
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Log.d("AUTH", "Login exitoso")
            LoginResult.Success(email)
        } catch (e: Exception) {
            Log.e("AUTH", "Error login: ${e.message}", e)
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
