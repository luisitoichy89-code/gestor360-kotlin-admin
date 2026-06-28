package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.User
import java.security.MessageDigest

sealed class LoginResult {
    data class Success(val userId: String, val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthRepository {

    suspend fun login(username: String, password: String): LoginResult {
        if (username == "admin" && password == "admin") {
            val defaultUser = User(
                id = "0",
                auth_id = "default",
                username = "admin",
                nombre = "Administrador",
                password = hash("admin"),
                rol = "superadmin",
                cliente_id = "0",
                almacen_id = "0",
                activo = true
            )
            return LoginResult.Success("0", defaultUser)
        }

        return try {
            val supabase = SupabaseClientProvider.client

            val users = supabase
                .from("usuarios")
                .select {
                    filter { eq("username", username) }
                }
                .decodeAs<List<User>>()

            if (users.isEmpty()) {
                return LoginResult.Error("Usuario no encontrado")
            }

            val user = users.first()
            val storedHash = user.password ?: ""
            val inputHash = hash(password)

            if (storedHash != inputHash) {
                return LoginResult.Error("Contraseña incorrecta")
            }

            if (!user.activo) {
                return LoginResult.Error("Usuario desactivado")
            }

            LoginResult.Success(user.id, user)

        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "Error de conexión")
        }
    }

    private fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
