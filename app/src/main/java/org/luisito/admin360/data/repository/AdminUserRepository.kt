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

    private val supabaseUrl = "https://duspeazziwxptcrignju.supabase.co"
    private val supabaseKey = "sb_secret_yrX5riDfgP5rJ76D5FWktg_ZZPl5jjG"

    private val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
            })
        }
    }

    suspend fun getUsers(clienteId: String): List<AdminUser> {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
                .decodeAs<List<AdminUser>>()
        } catch (e: Exception) {
            emptyList()
        }
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
            
            // 1. Crear usuario en Supabase Auth vía API REST
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

            // 2. Insertar en la tabla usuarios
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios").insert(
                mapOf(
                    "auth_id" to authId,
                    "cliente_id" to clienteId,
                    "username" to username,
                    "nombre" to nombre,
                    "rol" to rol,
                    "almacen_id" to almacenId,
                    "activo" to true
                )
            )
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteUser(id: Int): Boolean {
        return try {
            val supabase = SupabaseClientProvider.client
            supabase.from("usuarios")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            true
        } catch (e: Exception) {
            false
        }
    }
}
