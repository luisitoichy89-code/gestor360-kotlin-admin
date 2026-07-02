package org.luisito.admin360.data.repository

import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.filter.FilterOperator
import org.luisito.admin360.data.models.Negocio
import org.luisito.admin360.data.remote.SupabaseProvider

class NegocioRepository {
    
    suspend fun getNegocios(clienteId: String): Result<List<Negocio>> {
        return try {
            val response = SupabaseProvider.client
                .from("negocios")
                .select {
                    filter {
                        eq("cliente_id", clienteId)
                    }
                }
            Result.success(response.decodeList<Negocio>())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun createNegocio(
        nombre: String,
        direccion: String,
        telefono: String,
        clienteId: String
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("negocios")
                .insert(
                    mapOf(
                        "nombre" to nombre,
                        "direccion" to direccion,
                        "telefono" to telefono,
                        "cliente_id" to clienteId
                    )
                )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateNegocio(
        id: String,
        nombre: String,
        direccion: String,
        telefono: String
    ): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("negocios")
                .update(
                    mapOf(
                        "nombre" to nombre,
                        "direccion" to direccion,
                        "telefono" to telefono
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
    
    suspend fun deleteNegocio(id: String): Result<Unit> {
        return try {
            SupabaseProvider.client
                .from("negocios")
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
