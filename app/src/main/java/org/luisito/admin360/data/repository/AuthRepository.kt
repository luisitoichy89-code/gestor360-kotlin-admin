package org.luisito.admin360.data.repository

import android.content.Context
import android.content.SharedPreferences
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.luisito.admin360.data.SupabaseClientProvider
import java.security.MessageDigest

sealed class LoginResult {
    data class Success(val userId: String, val mode: String) : LoginResult()
    data class Error(val message: String) : LoginResult()
    data object ResetRequired : LoginResult()
}

class AuthRepository(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("g360_auth_cache", Context.MODE_PRIVATE)

    private fun sha256(input: String): String {
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input.toByteArray()).joinToString("") { "%02x".format(it) }
    }

    suspend fun login(username: String, password: String, clienteId: String): LoginResult {
        if (clienteId.isBlank()) {
            return LoginResult.Error("Dispositivo no activado. Contacta al administrador.")
        }

        // Email sintético igual que app_cliente.py: username@{cliente_id[:8]}.gestor360.local
        val synthEmail = "$username@${clienteId.take(8)}.gestor360.local"
        val supabase = SupabaseClientProvider.client

        // 1) Intentar login online con Supabase Auth
        var onlineSuccess = false
        try {
            withContext(Dispatchers.IO) {
                supabase.auth.signInWith(Email) {
                    this.email = synthEmail                    this.password = password
                }
            }
            onlineSuccess = true

            // Cachear hash + timestamp para fallback offline (válido 30 días)
            prefs.edit()
                .putString("cached_hash_$username", sha256(password))
                .putLong("last_online_login_$username", System.currentTimeMillis())
                .apply()
        } catch (_: Exception) {
            // Sin red o credenciales inválidas online → probamos modo offline abajo
        }

        // 2) Fallback offline: verificar contra hash cacheado localmente
        if (!onlineSuccess) {
            val cachedHash = prefs.getString("cached_hash_$username", null)
            val lastOnline = prefs.getLong("last_online_login_$username", 0L)

            if (cachedHash == null || lastOnline == 0L) {
                return LoginResult.Error("Sin conexión y sin sesión cacheada. Conecta a internet.")
            }

            // Verificar caducidad de 30 días (igual que app_cliente.py)
            val daysSinceLastOnline = (System.currentTimeMillis() - lastOnline) / (1000 * 60 * 60 * 24)
            if (daysSinceLastOnline > 30) {
                return LoginResult.Error("Sesión offline vencida (30 días). Conecta a internet para renovar.")
            }

            if (sha256(password) != cachedHash) {
                return LoginResult.Error("Contraseña incorrecta")
            }
        }

        // 3) Verificar reset_requested en Supabase (flujo "Olvidé Contraseña")
        try {
            val userRow = withContext(Dispatchers.IO) {
                supabase.from("usuarios")
                    .select { filter { eq("username", username); eq("cliente_id", clienteId) } }
                    .decodeAs<List<Map<String, Any?>>>()
                    .firstOrNull()
            }
            val resetRequested = userRow?.get("reset_requested") as? Boolean ?: false
            if (resetRequested) {
                return LoginResult.ResetRequired
            }
        } catch (_: Exception) {
            // Si falla la consulta, continuamos con login normal
        }
        val mode = if (onlineSuccess) "online" else "offline"
        return LoginResult.Success(synthEmail, mode)
    }

    /**
     * Actualiza contraseña en Supabase Auth tras confirmación de reset por admin.
     * Se llama desde la pantalla de "Crear Nueva Contraseña" después de que el admin
     * confirmó el reset en el submenú Pendientes.
     */
    suspend fun updatePasswordAfterReset(newPassword: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                SupabaseClientProvider.client.auth.updateUser {
                    this.password = newPassword
                }
            }
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Marca reset_requested = true para un usuario específico.
     * Se llama cuando el trabajador presiona "Olvidé Contraseña" en la pantalla de login.
     */
    suspend fun requestPasswordReset(username: String, clienteId: String): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                SupabaseClientProvider.client.from("usuarios")
                    .update(mapOf("reset_requested" to true)) {
                        filter {
                            eq("username", username)
                            eq("cliente_id", clienteId)
                        }
                    }
            }
            true
        } catch (_: Exception) {
            false
        }
    }
}
