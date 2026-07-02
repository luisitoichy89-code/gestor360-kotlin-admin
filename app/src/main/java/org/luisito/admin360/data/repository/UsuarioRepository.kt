package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import io.github.jan.supabase.postgrest.query.eq
import kotlinx.serialization.Serializable
import org.luisito.admin360.data.remote.SupabaseProvider

@Serializable
data class User(
    val id: String? = null,
    val username: String,
    val nombre: String? = null,
    val rol: String,
    val almacen_id: String,
    val activo: Boolean = true
)

class UsuarioRepository {

    suspend fun getUsuarios(clienteId: String): Result<List<User>> {
        return try {
            val result = SupabaseProvider.client
                .from("usuarios")
                .select {
                    filter { eq("cliente_id", clienteId) }
                }

            Result.success(result.decodeList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUsuario(
        username: String,
        nombre: String,
        password: String,
        rol: String,
        clienteId: String,
        almacenId: String
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
                .insert(
                    mapOf(
                        "username" to username,
                        "nombre" to nombre,
                        "password" to password,
                        "rol" to rol,
                        "cliente_id" to clienteId,
                        "almacen_id" to almacenId
                    )
                )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateUsuario(
        id: String,
        username: String,
        nombre: String,
        rol: String,
        almacenId: String,
        activo: Boolean
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
                .update(
                    mapOf(
                        "username" to username,
                        "nombre" to nombre,
                        "rol" to rol,
                        "almacen_id" to almacenId,
                        "activo" to activo
                    )
                ) {
                    filter { eq("id", id) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUsuario(id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
                .delete {
                    filter { eq("id", id) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
