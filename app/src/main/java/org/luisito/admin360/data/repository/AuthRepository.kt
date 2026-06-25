package org.luisito.admin360.data.repository

import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.luisito.admin360.data.SupabaseClientProvider

sealed class LoginResult {
    data class Success(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthRepository {
    suspend fun login(username: String, password: String, clienteId: String): LoginResult {
        if (clienteId.isBlank()) return LoginResult.Error("Negocio no seleccionado")
        val synthEmail = "$username@${clienteId.take(8)}.gestor360.local"
        return runCatching {
            withContext(Dispatchers.IO) {
                SupabaseClientProvider.client.auth.signInWith(Email) {
                    this.email = synthEmail
                    this.password = password
                }
            }
            LoginResult.Success(synthEmail)
        }.getOrElse { LoginResult.Error(it.message ?: "Error de conexión") }
    }
}
