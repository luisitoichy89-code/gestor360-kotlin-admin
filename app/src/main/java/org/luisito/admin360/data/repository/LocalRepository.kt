package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import io.github.jan.supabase.postgrest.query.eq
import kotlinx.serialization.Serializable
import org.luisito.admin360.data.remote.SupabaseProvider

@Serializable
data class Local(
    val id: String? = null,
    val cliente_id: String,
    val nombre: String,
    val activo: Boolean = true
)

class LocalRepository {

    suspend fun getLocales(clienteId: String): Result<List<Local>> {
        return try {
            val result = SupabaseProvider.client
                .from("locales")
                .select {
                    filter { eq("cliente_id", clienteId) }
                }

            Result.success(result.decodeList())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createLocal(clienteId: String, nombre: String): Result<Unit> {
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

    suspend fun updateLocal(id: String, nombre: String, activo: Boolean): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .update(
                    mapOf(
                        "nombre" to nombre,
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

    suspend fun deleteLocal(id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("locales")
                .delete {
                    filter { eq("id", id) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
