package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.luisito.admin360.data.SupabaseClientProvider
import org.luisito.admin360.data.models.AdminUser
import org.luisito.admin360.BuildConfig

@Serializable
data class CreateUserRequest(
    val email: String,
    val password: String,
    val email_confirm: Boolean = true
)

@Serializable
data class CreateUserResponse(
    val id: String,
    val email: String
)

class AdminUserRepository {

    private val supabaseUrl = BuildConfig.SUPABASE_URL
    private val supabaseKey = BuildConfig.SUPABASE_KEY

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json { ignoreUnknownKeys = true })
        }
    }
    suspend fun getUsers(clienteId: String): List<AdminUser> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios")
                .select { filter { eq("cliente_id", clienteId) } }
                .decodeAs<List<AdminUser>>()
        } catch (e: Exception) { emptyList() }
    }

    suspend fun createUser(
        clienteId: String,
        username: String,
        password: String,
        nombre: String,
        rol: String,
        almacenId: String,
        deviceId: String? = null
    ): Boolean {
        return try {
            // Email sintético igual que app_admin.py: username@{cliente_id[:8]}.gestor360.local
            val synthEmail = "$username@${clienteId.take(8)}.gestor360.local"

            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/admin/users") {
                contentType(ContentType.Application.Json)
                headers {
                    append("apikey", supabaseKey)
                    append("Authorization", "Bearer $supabaseKey")
                }
                setBody(CreateUserRequest(synthEmail, password))
            }

            if (response.status.value !in 200..299) return false

            val userResponse: CreateUserResponse = response.body()
            val authId = userResponse.id

            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios").insert(mapOf(
                "auth_id" to authId,
                "cliente_id" to clienteId,
                "username" to username,
                "nombre" to nombre,
                "rol" to rol,
                "almacen_id" to almacenId,
                "activo" to true,
                "device_id" to deviceId,
                "reset_requested" to false
            ))
            true        } catch (e: Exception) { false }
    }

    suspend fun updateUser(
        id: Int,
        username: String,
        nombre: String,
        rol: String,
        almacenId: String,
        activo: Boolean,
        deviceId: String? = null
    ): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            val data = mutableMapOf<String, Any?>(
                "username" to username,
                "nombre" to nombre,
                "rol" to rol,
                "almacen_id" to almacenId,
                "activo" to activo
            )
            if (deviceId != null) data["device_id"] = deviceId
            supabase.from("usuarios")
                .update(data) { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }

    suspend fun deleteUser(id: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios").delete { filter { eq("id", id) } }
            true
        } catch (e: Exception) { false }
    }

    // Trabajador presiona "Olvidé Contraseña" → marca reset_requested = true
    suspend fun requestPasswordReset(userId: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios")
                .update(mapOf("reset_requested" to true)) { filter { eq("id", userId) } }
            true
        } catch (e: Exception) { false }
    }

    // Admin confirma reset desde submenú Pendientes → reset_requested = false
    suspend fun confirmPasswordReset(userId: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client            supabase.from("usuarios")
                .update(mapOf("reset_requested" to false)) { filter { eq("id", userId) } }
            true
        } catch (e: Exception) { false }
    }

    // Obtener usuarios con reset pendiente (submenú Pendientes del admin)
    suspend fun getPendingResets(clienteId: String): List<AdminUser> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios")
                .select { filter { eq("cliente_id", clienteId); eq("reset_requested", true) } }
                .decodeAs<List<AdminUser>>()
        } catch (e: Exception) { emptyList() }
    }
}
