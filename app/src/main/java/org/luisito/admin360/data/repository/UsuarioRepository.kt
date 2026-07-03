package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import org.luisito.admin360.data.models.User
import org.luisito.admin360.data.remote.SupabaseProvider

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
            val payload = buildJsonObject {
                put("username", username)
                put("nombre", nombre)
                put("pin", pin)
                put("rol", rol)
                put("cliente_id", clienteId)
                put("almacen_id", almacenId)
                put("activo", true)
            }
            SupabaseProvider.client
                .from("usuarios")
                .insert(payload)
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
            val payload = buildJsonObject {
                put("username", username)
                put("nombre", nombre)
                put("rol", rol)
                put("almacen_id", almacenId)
                put("activo", activo)
            }
            SupabaseProvider.client
                .from("usuarios")
                .update(payload) {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

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
