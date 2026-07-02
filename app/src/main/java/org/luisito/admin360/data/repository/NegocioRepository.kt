import io.github.jan.supabase.postgrest.query.eq
package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.data.remote.SupabaseProvider

class NegocioRepository {

    suspend fun getNegocios(): Result<List<Negocio>> {
        return try {
            val response = SupabaseProvider.client
                .from("clientes")
                .select()

            Result.success(response.decodeList<Negocio>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNegocio(id = null, nombre: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .insert(
                    Negocio(id = null, 
                        nombre_negocio = nombre,
                        activo = true
                    )
                )

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNegocio(id = null, 
        id: String,
        nombre: String,
        activo: Boolean
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .update(
                    mapOf(
                        "nombre_negocio" to nombre,
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

    suspend fun deleteNegocio(id = null, id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
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
