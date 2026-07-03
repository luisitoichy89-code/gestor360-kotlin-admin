package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.data.remote.SupabaseProvider

/** Opera sobre la tabla real "clientes" (no existe tabla "negocios"). */
class NegocioRepository {

    suspend fun getAllNegocios(): Result<List<Negocio>> {
        return try {
            val response = SupabaseProvider.client
                .from("clientes")
                .select()
            Result.success(response.decodeList<Negocio>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun createNegocio(nombre: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .insert(mapOf("nombre_negocio" to nombre))
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNegocio(id: String, nombre: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
                .update(mapOf("nombre_negocio" to nombre)) {
                    filter {
                        eq("id", id)
                    }
                }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setActivo(id: String, activo: Boolean): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("clientes")
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

    suspend fun deleteNegocio(id: String): Result<Unit> {
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
