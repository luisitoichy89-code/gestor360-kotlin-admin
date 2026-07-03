package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.remote.SupabaseProvider

class LocalRepository {

    suspend fun getLocales(clienteId: String): Result<List<Local>> {
        return try {
            val response = SupabaseProvider.client
                .from("locales")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
            Result.success(response.decodeList<Local>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createLocal(nombre: String, clienteId: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .insert(
                    mapOf(
                        "nombre" to nombre,
                        "cliente_id" to clienteId
                    )
                )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLocal(id: Long, nombre: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .update(mapOf("nombre" to nombre)) {
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
                .from("locales")
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

    suspend fun deleteLocal(id: Long): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
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
