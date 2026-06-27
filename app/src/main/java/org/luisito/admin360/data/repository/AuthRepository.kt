package org.luisito.admin360.data.repository

import org.luisito.admin360.data.SupabaseClientProvider

sealed class LoginResult {
    data class Success(val userId: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthRepository {
    suspend fun login(username: String, password: String): LoginResult {
        return try {
            val supabase = SupabaseClientProvider.client
            // Consulta simple sin filtros complejos
            val response = supabase.from("usuarios")
                .select()
                .execute()
            val users = response.dataAs<List<Map<String, Any>>>()
            val user = users.find { it["username"] == username }
            if (user == null) {
                return LoginResult.Error("Usuario no encontrado")
            }
            val storedHash = user["password"] as? String ?: ""
            val inputHash = hash(password)
            if (storedHash == inputHash) {
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

    suspend fun sendPasswordRecovery(email: String): Boolean {
        return false
    }
}
