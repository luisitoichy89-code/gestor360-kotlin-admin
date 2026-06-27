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
            SupabaseClientProvider.client
                .from("usuarios")
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
        almacenId: String
    ): Boolean {
        return try {
            val email = "$username@gestor360.local"
            val response: HttpResponse = client.post("$supabaseUrl/auth/v1/admin/users") {
                contentType(ContentType.Application.Json)
                headers {
                    append("apikey", supabaseKey)
                    append("Authorization", "Bearer $supabaseKey")
                }
                setBody(CreateUserRequest(email, password))
            }

            if (response.status.value !in 200..299) {
                return false
            }

            val userResponse: CreateUserResponse = response.body()
            val authId = userResponse.id

            SupabaseClientProvider.client
                .from("usuarios")
                .insert(mapOf(
                    "auth_id" to authId,
                    "cliente_id" to clienteId,
                    "username" to username,
                    "nombre" to nombre,
                    "rol" to rol,
                    "almacen_id" to almacenId,
                    "activo" to true
                )) {
                    select()
                }
            true
        } catch (e: Exception) { false }
    }

    suspend fun updateUser(id: String, username: String, nombre: String, rol: String, almacenId: String, activo: Boolean): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .update(mapOf(
                    "username" to username,
                    "nombre" to nombre,
                    "rol" to rol,
                    "almacen_id" to almacenId,
                    "activo" to activo
                )) {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) { false }
    }

    suspend fun deleteUser(id: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .delete {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) { false }
    }

    suspend fun approveUser(id: String): Boolean {
        return try {
            SupabaseClientProvider.client
                .from("usuarios")
                .update(mapOf("device_approved" to true)) {
                    filter { eq("id", id) }
                }
            true
        } catch (e: Exception) { false }
    }
}
