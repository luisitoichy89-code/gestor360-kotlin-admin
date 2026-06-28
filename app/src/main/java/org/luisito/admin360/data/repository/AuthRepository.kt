package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.rpc
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.User
import java.security.MessageDigest

sealed class LoginResult {
    data class Success(val userId: String, val user: User) : LoginResult()
    data class Error(val message: String) : LoginResult()
}

class AuthRepository {

    suspend fun login(username: String, password: String): LoginResult {
        // 🔓 LOGIN POR DEFECTO (solo para pruebas)
        if (username == "admin" && password == "admin") {
            val defaultUser = User(
                id = "00000000-0000-0000-0000-000000000000",
                auth_id = "default",
                username = "admin",
                nombre = "Administrador",
                password = hash("admin"),
                rol = "superadmin",
                cliente_id = "00000000-0000-0000-0000-000000000000",
                almacen_id = "0",  // ← CORREGIDO
                activo = true
            )
            return LoginResult.Success("0", defaultUser)
        }

        // 🔐 LOGIN CONTRA SUPABASE CON VALIDACIÓN DE LICENCIA
        return try {
            val supabase = SupabaseClientProvider.client

            // 1. Buscar usuario por username
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

            // 2. Verificar contraseña
            val storedHash = user.password ?: ""
            val inputHash = hash(password)

            if (storedHash != inputHash) {
                return LoginResult.Error("Contraseña incorrecta")
            }

            // 3. Verificar que el usuario esté activo
            if (!user.activo) {
                return LoginResult.Error("Usuario desactivado. Contacte al administrador.")
            }

            // 4. VERIFICAR LICENCIA ACTIVA
            val canLogin = verifyLicense(user.auth_id ?: "")
            if (!canLogin) {
                return LoginResult.Error("Licencia expirada. Contacte al administrador.")
            }

            // 5. Login exitoso
            LoginResult.Success(user.id, user)

        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "Error de conexión")
        }
    }

    // FUNCIÓN CORREGIDA: Verificar licencia activa usando .postgrest.rpc()
    private suspend fun verifyLicense(authId: String): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            val result = supabase.rpc(
                function = "usuario_puede_loguearse",
                parameters = mapOf("p_auth_uid" to authId)
            )
            result.decodeAs<Boolean>()
        } catch (e: Exception) {
            false
        }
    }

    private fun hash(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256")
            .digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    suspend fun sendPasswordRecovery(email: String): Boolean {
        return false
    }
}
