package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.remote.SupabaseProvider

class UsuarioRepository {

    suspend fun getUsuarios(clienteId: String): Result<List<User>> {
        return try {
            val response = SupabaseProvider.client
                .from("usuarios")
                .select { eq("cliente_id", clienteId) }
            Result.success(response.decodeList<User>())
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
                        "almacen_id" to almacenId,
                        "activo" to true
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
                ) { eq("id", id) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUsuario(id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
                .delete { eq("id", id) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
