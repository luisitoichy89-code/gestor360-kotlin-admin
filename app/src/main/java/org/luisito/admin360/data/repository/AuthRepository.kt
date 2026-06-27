package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import io.github.jan.supabase.postgrest.query.eq
import org.luisito.admin360.data.SupabaseClientProvider

sealed class LoginResult {
    data class Success(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthRepository {
    suspend fun login(username: String, password: String): LoginResult {
        return try {
            val supabase = SupabaseClientProvider.client
            val response = supabase.from("usuarios")
                .select()
                .filter {
                    eq("username", username)
                }
                .decodeAs<List<Map<String, Any>>>()

            if (response.isEmpty()) {
                return LoginResult.Error("Usuario no encontrado")
            }

            val user = response.first()
            val storedHash = user["password"] as? String ?: ""
            val inputHash = hash(password)

            return if (storedHash == inputHash) {
                LoginResult.Success(user["id"] as? String ?: "")
            } else {
                LoginResult.Error("Contraseña incorrecta")
            }
        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "Error de conexión")
        }
    }

    private fun hash(password: String): String {
        val bytes = java.security.MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
