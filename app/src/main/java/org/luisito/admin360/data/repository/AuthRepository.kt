package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.models.LoginResult
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
a import java.util.UUID
import java.security.MessageDigest
import java.util.UUID

class AuthRepository {

    suspend fun login(username: String, password: String): LoginResult {
        // 🔓 LOGIN POR DEFECTO (solo para pruebas)
        if (username == "admin" && password == "admin") {
            val defaultUser = User(
                id = 0,
                auth_id = null,
                username = "admin",
                nombre = "Administrador",
                password = hash("admin"),
                rol = "superadmin",
                cliente_id = 0,
                almacen_id = 0,
                activo = true
            )
            return LoginResult.Success(0, defaultUser)
        }

        // 🔐 LOGIN CONTRA SUPABASE CON VALIDACIÓN DE LICENCIA
        return try {
            val supabase = SupabaseClientProvider.client

            // 1. Buscar usuario por username
            val users = supabase.postgrest.from("usuarios")
                .select(Columns.ALL) {
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
            val canLogin = verifyLicense(user.auth_id)
            if (!canLogin) {
                return LoginResult.Error("Licencia expirada. Contacte al administrador.")
            }

            // 5. Login exitoso
            LoginResult.Success(user.id, user)

        } catch (e: Exception) {
            LoginResult.Error(e.message ?: "Error de conexión")
        }
    }

    private suspend fun verifyLicense(authId: UUID?): Boolean {
        if (authId == null) return false
        return try {
            val supabase = SupabaseClientProvider.client
            val params = buildJsonObject {
                put("p_auth_uid", authId.toString())
            }
            val result = supabase.postgrest.rpc(
                function = "usuario_puede_loguearse",
                parameters = params
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
