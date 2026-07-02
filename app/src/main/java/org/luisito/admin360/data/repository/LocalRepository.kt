package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import io.github.jan.supabase.postgrest.query.Filter
import org.luisito.admin360.data.models.Local
import org.luisito.admin360.data.remote.SupabaseProvider

class LocalRepository {

    suspend fun getLocales(clienteId: String): Result<List<Local>> {
        return try {
            val response = SupabaseProvider.client
                .from("locales")
                .select {
                    filter { Filter.eq("cliente_id", clienteId) }
                }
            Result.success(response.decodeList<Local>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createLocal(
        clienteId: String,
        nombre: String
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .insert(
                    mapOf(
                        "cliente_id" to clienteId,
                        "nombre" to nombre,
                        "activo" to true
                    )
                )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateLocal(
        id: String,
        nombre: String,
        activo: Boolean
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .update(
                    mapOf(
                        "nombre" to nombre,
                        "activo" to activo
                    )
                ) {
                    filter { Filter.eq("id", id) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteLocal(id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .delete {
                    filter { Filter.eq("id", id) }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
