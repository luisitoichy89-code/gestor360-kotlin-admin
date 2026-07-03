package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.remote.SupabaseProvider

/**
 * Usuarios (admin/seller) autenticados solo por PIN + Android ID, sin cuenta en
 * Supabase Auth (auth_id queda null). El primer acceso en el dispositivo valida
 * contra esta tabla; luego la app cliente trabaja offline con caché local.
 */
class UsuarioRepository {

    suspend fun getUsuarios(clienteId: String): Result<List<User>> {
        return try {
            val response = SupabaseProvider.client
                .from("usuarios")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
            Result.success(response.decodeList<User>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createUsuario(
        username: String,
        nombre: String,
        pin: String,
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
                        "pin" to pin,
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
        id: Long,
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
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /** Cambiar el PIN es una acción separada del resto de la edición, por seguridad. */
    suspend fun cambiarPin(id: Long, nuevoPin: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
                .update(mapOf("pin" to nuevoPin)) {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setActivo(id: Long, activo: Boolean): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
                .update(mapOf("activo" to activo)) {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteUsuario(id: Long): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("usuarios")
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
