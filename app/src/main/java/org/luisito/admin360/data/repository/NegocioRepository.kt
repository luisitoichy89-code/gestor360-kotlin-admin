package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter
import io.github.jan.supabase.postgrest.query.eq
import kotlinx.serialization.Serializable
import org.luisito.admin360.data.remote.SupabaseProvider

@Serializable
data class Negocio(
    val id: String? = null,
    val nombre_negocio: String,
    val activo: Boolean = true
)

class NegocioRepository {

    suspend fun getNegocios(): Result<List<Negocio>> {
        return try {
            val result = SupabaseProvider.client
                .from("clientes")
                .select()

            Result.success(result.decodeList<Negocio>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNegocio(nombre: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .insert(
                    Negocio(nombre_negocio = nombre)
                )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNegocio(id: String, nombre: String, activo: Boolean): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .update(
                    Negocio(nombre_negocio = nombre, activo = activo)
                ) {
                    filter { eq("id", id) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNegocio(id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .delete {
                    filter { eq("id", id) }
                }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
